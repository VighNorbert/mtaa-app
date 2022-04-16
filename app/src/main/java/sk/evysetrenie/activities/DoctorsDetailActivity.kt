package sk.evysetrenie.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import sk.evysetrenie.api.interfaces.AvatarReader
import sk.evysetrenie.api.interfaces.DoctorsDetailReader
import sk.evysetrenie.api.interfaces.FavouriteSetter
import sk.evysetrenie.R
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.DoctorsService
import sk.evysetrenie.api.model.WorkSchedule
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.DoctorsDetailResponse
import java.util.*

class DoctorsDetailActivity() : ReturningActivity(), FavouriteSetter, DoctorsDetailReader,
    AvatarReader {

    private var doctorId: Int? = 0
    private var isFavourite: Boolean = false

    private lateinit var doctorAvatarImageView: ImageView
    private lateinit var doctorStar: ImageView
    private lateinit var doctorNameText: TextView
    private lateinit var doctorSpecialisationText: TextView
    private lateinit var doctorScheduleDayText: TextView
    private lateinit var doctorScheduleTimeText: TextView
    private lateinit var doctorAddressText: TextView
    private lateinit var doctorContactText: TextView
    private lateinit var doctorDescriptionText: TextView

    private var flag: Int = 0

    private lateinit var detailAppointmentLayout: LinearLayout
    private lateinit var detailAppointmentDate: TextView

    private var pickedDay: Int = 0
    private var pickedMonth: Int = 0
    private var pickedYear: Int = 0

    private var availableDays = mutableListOf<Calendar>()

    private val dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        checkLoggedIn()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_doctors_detail)
        }
        super.onCreate(savedInstanceState)

        doctorAvatarImageView = findViewById(R.id.doctorAvatarImageView)
        doctorStar = findViewById(R.id.doctorStar)
        doctorNameText = findViewById(R.id.doctorNameText)
        doctorSpecialisationText = findViewById(R.id.doctorSpecialisationText)
        doctorScheduleDayText = findViewById(R.id.doctorScheduleDayText)
        doctorScheduleTimeText = findViewById(R.id.doctorScheduleTimeText)
        doctorAddressText = findViewById(R.id.doctorAddressText)
        doctorContactText = findViewById(R.id.doctorContactText)
        doctorDescriptionText = findViewById(R.id.doctorDescriptionText)

        detailAppointmentLayout = findViewById(R.id.detailAppointmentLayout)
        detailAppointmentDate = findViewById(R.id.detailAppointmentDate)

        val b: Bundle? = intent.extras
        doctorId = b?.getInt("id")
        if (doctorId != null) {
            loadingDialog.open()
            DoctorsService().getDetail(doctorId!!, this)
            DoctorsService().getAvatar(doctorId!!, this)
        }
        doctorStar.setOnClickListener{
            if (isFavourite) {
                doctorStar.setImageResource(R.drawable.star_unfilled)
                doctorStar.contentDescription = "Lekár nie je obľúbený"
                if (doctorId != null) {
                    DoctorsService().removeFromFavourites(doctorId!!,this, this)
                }
            }
            else {
                doctorStar.setImageResource(R.drawable.star_filled)
                doctorStar.contentDescription = "Lekár je obľúbený"
                if (doctorId != null) {
                    DoctorsService().addToFavourites(doctorId!!,this, this)
                }
            }
            isFavourite = !isFavourite
        }
        dpd.setOnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            detailAppointmentDate.text = "Dátum: ${dayOfMonth}. ${monthOfYear + 1}. $year"
            pickedDay = dayOfMonth
            pickedMonth = monthOfYear+1
            pickedYear = year
        }
    }

    @SuppressLint("SetTextI18n")
    override fun dataReceived(doctor: DoctorsDetailResponse) {
        val name = "${doctor.title} ${doctor.name} ${doctor.surname}"
        val spannableString = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldSpan, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        doctorNameText.text = spannableString
        doctorSpecialisationText.text = doctor.specialisation.title

        doctor.schedules.forEach { ws ->
            if (ws.weekday == 0)
                ws.weekday = 7
        }
        val sortedSchedules = doctor.schedules.sortedWith(compareBy<WorkSchedule> { it.weekday }.thenBy { it.time_from })
        printSchedules(sortedSchedules)

        doctorAddressText.text = "${doctor.address}, ${doctor.city}"
        doctorContactText.text = doctor.phone
        doctorDescriptionText.text = doctor.description
        isFavourite = doctor.is_favourite
        if (isFavourite) {
            doctorStar.setImageResource(R.drawable.star_filled)
        }
        else {
            doctorStar.setImageResource(R.drawable.star_unfilled)
        }
    }

    fun getAvailableDates(v: View) {
        loadingDialog.open()
        val month = Calendar.getInstance().get(Calendar.MONTH)+1
        val year = Calendar.getInstance().get(Calendar.YEAR)
        println(month)
        println(year)
        DoctorsService().getDates(doctorId!!, month, year, this)
        DoctorsService().getDates(doctorId!!, month+1, year, this)
    }

    fun datesReceived(days: List<Int>, month: Int, year: Int) {
        var calendar: Calendar
        for (day in days) {
            calendar = Calendar.getInstance()
            calendar.set(year, month-1, day)
            availableDays.add(calendar)
        }
        flag++
        if (flag == 2) {
            flag = 0
            dpd.selectableDays = availableDays.toTypedArray()
            dpd.show(supportFragmentManager, "dpd")
            loadingDialog.dismiss()
        }
    }

    fun printSchedules(schedules: List<WorkSchedule>) {
        var daysText = ""
        var timesText = ""
        val days = mapOf(1 to "Pondelok", 2 to "Utorok", 3 to "Streda", 4 to "Štvrtok", 5 to "Piatok", 6 to "Sobota", 7 to "Nedeľa")
        var currentDay = 0
        for (schedule in schedules) {
            if (schedule.weekday > currentDay) {
                currentDay = schedule.weekday
                daysText += days[currentDay] + ": "
            }
            daysText += "\n"
            timesText += schedule.time_from.dropLast(3) + " - " + schedule.time_to.dropLast(3) + "\n"
        }
        doctorScheduleDayText.text = daysText.dropLast(1)
        doctorScheduleTimeText.text = timesText.dropLast(1)
    }

    override fun avatarReceived(bmp: Bitmap?) {
        if (bmp == null) {
            doctorAvatarImageView.setImageResource(R.drawable.default_doctor_avatar)
        }
        else {
            doctorAvatarImageView.setImageBitmap(bmp)
        }
        loadingDialog.dismiss()
    }

    override fun showError(error: ApiError) {
        Toast.makeText(this.applicationContext, error.message, Toast.LENGTH_SHORT).show()
        loadingDialog.dismiss()
    }

    fun onMakeAppointmentClick(v: View) {
        if (detailAppointmentLayout.isVisible) {
            detailAppointmentLayout.visibility = View.GONE
        }
        else {
            detailAppointmentLayout.visibility = View.VISIBLE
        }
    }

}