package sk.evysetrenie

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import okhttp3.ResponseBody
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.DoctorsService
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.DoctorsDetailResponse

class DoctorsDetailActivity() : ReturningActivity(), FavouriteSetter, DoctorsDetailReader {

    private var isFavourite: Boolean = false

    private lateinit var doctorAvatarImageView: ImageView
    private lateinit var doctorStar: ImageView
    private lateinit var doctorNameText: TextView
    private lateinit var doctorSpecialisationText: TextView
    private lateinit var doctorScheduleText: TextView
    private lateinit var doctorAddressText: TextView
    private lateinit var doctorContactText: TextView
    private lateinit var doctorDescriptionText: TextView

    private lateinit var detailAppointmentLayout: LinearLayout

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
        doctorScheduleText = findViewById(R.id.doctorScheduleText)
        doctorAddressText = findViewById(R.id.doctorAddressText)
        doctorContactText = findViewById(R.id.doctorContactText)
        doctorDescriptionText = findViewById(R.id.doctorDescriptionText)

        detailAppointmentLayout = findViewById(R.id.detailAppointmentLayout)

        val b: Bundle? = intent.extras
        val doctorId = b?.getInt("id")
        if (doctorId != null) {
            loadingDialog.open()
            DoctorsService().getDetail(doctorId, this)
            DoctorsService().getAvatar(doctorId, this)
        }
        doctorStar.setOnClickListener{
            if (isFavourite) {
                doctorStar.setImageResource(R.drawable.star_unfilled)
                doctorStar.contentDescription = "Lekár nie je obľúbený"
                if (doctorId != null) {
                    DoctorsService().removeFromFavourites(doctorId,this, this)
                }
            }
            else {
                doctorStar.setImageResource(R.drawable.star_filled)
                doctorStar.contentDescription = "Lekár je obľúbený"
                if (doctorId != null) {
                    DoctorsService().addToFavourites(doctorId,this, this)
                }
            }
            isFavourite = !isFavourite
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
        doctorScheduleText.text = "Pondelok\nUtorok\nStreda\nStvrtok\nPiatok\nSobota\nNedela"
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
        loadingDialog.dismiss()
    }

    fun avatarReceived(bmp: Bitmap?) {
        if (bmp == null) {
            doctorAvatarImageView.setImageResource(R.drawable.default_doctor_avatar)
        }
        else {
            doctorAvatarImageView.setImageBitmap(bmp)
        }
    }

    override fun showError(error: ApiError) {
        Toast.makeText(this.applicationContext, error.message, Toast.LENGTH_SHORT).show()
        loadingDialog.dismiss()
    }

    fun changeFavourite() {

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