package sk.evysetrenie

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.DoctorsService
import sk.evysetrenie.api.Validator
import sk.evysetrenie.api.interfaces.AvatarReader
import sk.evysetrenie.api.interfaces.DoctorsDetailReader
import sk.evysetrenie.api.interfaces.FavouriteSetter
import sk.evysetrenie.api.model.WorkSchedule
import sk.evysetrenie.api.model.contracts.requests.AppointmentRequest
import sk.evysetrenie.api.model.contracts.requests.DoctorsRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.AppointmentTimesResponse
import sk.evysetrenie.api.model.contracts.responses.DoctorsDetailResponse
import sk.evysetrenie.dialogs.AppointmentPickerDialog
import java.util.*


class DoctorsDetailActivity() : ReturningActivity(), FavouriteSetter, DoctorsDetailReader,
    AvatarReader {

    private var doctorId: Int = 0
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
    private lateinit var detailAppointmentTime: TextView
    private lateinit var detailAppointmentTypeText: TextView
    private lateinit var detailAppointmentDescriptionLayout: TextInputLayout
    private lateinit var detailAppointmentDescriptionText: TextInputEditText
    private lateinit var detailAppointmentSubmit: Button

    private var pickedDay: Int = 0
    private var pickedMonth: Int = 0
    private var pickedYear: Int = 0
    private var pickedAppointmentId = 0

    private var availableDays = mutableListOf<Calendar>()

    private val datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog()
    private lateinit var appointmentPickerDialog: AppointmentPickerDialog

    private val validator: Validator = Validator()

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
        detailAppointmentTime = findViewById(R.id.detailAppointmentTime)
        detailAppointmentTypeText = findViewById(R.id.detailAppointmentTypeText)
        detailAppointmentDescriptionLayout = findViewById(R.id.detailAppointmentDescriptionLayout)
        detailAppointmentDescriptionText = findViewById(R.id.detailAppointmentDescriptionText)
        detailAppointmentSubmit = findViewById(R.id.detailAppointmentSubmit)

        val b: Bundle? = intent.extras
        doctorId = b?.getInt("id")!!
        loadingDialog.open()
        DoctorsService().getDetail(doctorId, this)
        DoctorsService().getAvatar(doctorId, this)
        doctorStar.setOnClickListener{
            if (isFavourite) {
                doctorStar.setImageResource(R.drawable.star_unfilled)
                doctorStar.contentDescription = "Lekár nie je obľúbený"
                DoctorsService().removeFromFavourites(doctorId,this, this)
            }
            else {
                doctorStar.setImageResource(R.drawable.star_filled)
                doctorStar.contentDescription = "Lekár je obľúbený"
                DoctorsService().addToFavourites(doctorId,this, this)
            }
            isFavourite = !isFavourite
        }
        datePickerDialog.setOnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            detailAppointmentDate.text = "Dátum: ${dayOfMonth}. ${monthOfYear + 1}. $year"
            pickedDay = dayOfMonth
            pickedMonth = monthOfYear+1
            pickedYear = year
            pickedAppointmentId = 0
            detailAppointmentTime.isEnabled = true
            detailAppointmentTime.text = resources.getString(R.string.detail_time)
            detailAppointmentSubmit.isEnabled = false
            detailAppointmentTime.setTextColor(Color.BLACK)
        }
        appointmentPickerDialog = AppointmentPickerDialog(this)
        val types: List<String> = listOf("Fyzicky", "Online")
        val adapter = ArrayAdapter(this, R.layout.list_item, types)
        (detailAppointmentTypeText as? AutoCompleteTextView)?.setAdapter(adapter)
        detailAppointmentTypeText.addTextChangedListener(TextFieldValidation())
        detailAppointmentDescriptionText.addTextChangedListener(TextFieldValidation())
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
        DoctorsService().getDates(doctorId, month, year, this)
    }

    fun datesReceived(days: List<Int>, month: Int, year: Int) {
        var calendar: Calendar
        for (day in days) {
            calendar = Calendar.getInstance()
            calendar.set(year, month-1, day)
            availableDays.add(calendar)
        }
        flag++
        if (flag == 1) {
            DoctorsService().getDates(doctorId, month+1, year, this)
        }
        if (flag == 2) {
            flag = 0
            datePickerDialog.selectableDays = availableDays.toTypedArray()
            datePickerDialog.show(supportFragmentManager, "dpd")
            loadingDialog.dismiss()
        }
    }

    fun getAvailableTimes(v: View) {
        loadingDialog.open()
        DoctorsService().getTimes(doctorId, pickedDay, pickedMonth, pickedYear, this)
    }

    fun timesReceived(times: List<AppointmentTimesResponse>) {
        val sortedTimes = times.sortedWith(compareBy { it.time_from })
        appointmentPickerDialog.open(sortedTimes)
        loadingDialog.dismiss()
    }

    fun timePicked(appointmentId: Int?, time: String) {
        if (appointmentId != null) {
            pickedAppointmentId = appointmentId
        }
        else {
            println("null")
        }
        detailAppointmentTime.text = "Čas: $time"
        detailAppointmentSubmit.isEnabled = allFieldsFilled()
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

    fun allFieldsFilled(): Boolean {
        return validator.validateDescription(detailAppointmentDescriptionText, detailAppointmentDescriptionLayout)
            && detailAppointmentDate.text != resources.getString(R.string.detail_date)
            && detailAppointmentTime.text != resources.getString(R.string.detail_time)
            && detailAppointmentTypeText.text.isNotEmpty()
    }

    inner class TextFieldValidation() : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            detailAppointmentSubmit.isEnabled = allFieldsFilled()
        }
        override fun afterTextChanged(p0: Editable?) {}
    }

    fun onMakeAppointmentSubmit(v: View) {
        loadingDialog.open()
        val type: String
        if (detailAppointmentTypeText.text == "Fyzicky") {
            type = "F"
        }
        else {
            type = "O"
        }
        val description = detailAppointmentDescriptionText.text.toString()
        val ar = AppointmentRequest(
            description,
            type
        )
        DoctorsService().makeAppointment(doctorId, pickedAppointmentId, ar, this)
    }

    fun appointmentSuccess() {
        Toast.makeText(this.applicationContext, "Termín vyšetrenia bol úspešne zarezervovaný", Toast.LENGTH_LONG).show()
        val intent = Intent(this, AppointmentsActivity::class.java)
        startActivity(intent)
    }

}