package sk.evysetrenie

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.scale
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import sk.evysetrenie.api.AuthService
import sk.evysetrenie.api.SpecialisationService
import sk.evysetrenie.api.Validator
import sk.evysetrenie.api.model.Avatar
import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.contracts.requests.RegisterDoctorRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.math.min


class RegisterDoctorActivity : SpecialisationReader() {

    private lateinit var nameTextInput: TextInputEditText
    private lateinit var nameTextLayout: TextInputLayout

    private lateinit var surnameTextInput: TextInputEditText
    private lateinit var surnameTextLayout: TextInputLayout

    private lateinit var titleTextInput: TextInputEditText
    private lateinit var titleTextLayout: TextInputLayout

    private lateinit var emailTextInput: TextInputEditText
    private lateinit var emailTextLayout: TextInputLayout

    private lateinit var phoneTextInput: TextInputEditText
    private lateinit var phoneTextLayout: TextInputLayout

    private lateinit var passwordTextInput: TextInputEditText
    private lateinit var passwordTextLayout: TextInputLayout

    private lateinit var specialisationTextInput: TextView
    private lateinit var specialisationTextLayout: TextInputLayout

    private lateinit var addressTextInput: TextInputEditText
    private lateinit var addressTextLayout: TextInputLayout

    private lateinit var cityTextInput: TextInputEditText
    private lateinit var cityTextLayout: TextInputLayout

    private lateinit var descriptionTextInput: TextInputEditText
    private lateinit var descriptionTextLayout: TextInputLayout

    private lateinit var appointmentsLengthTextInput: TextInputEditText
    private lateinit var appointmentsLengthTextLayout: TextInputLayout

    private lateinit var errorAlert: TextView

    private lateinit var avatarImageView: ImageView
    private lateinit var noAvatarTextView: TextView

    private val validator: Validator = Validator()

    private var base64string : String? = null

    private lateinit var specialisationService: SpecialisationService

    // https://www.android--code.com/2020/06/android-kotlin-bitmap-crop-square.html
    private fun Bitmap.toSquare():Bitmap{
        // get the small side of bitmap
        val side = min(width,height)

        // calculate the x and y offset
        val xOffset = (width - side) /2
        val yOffset = (height - side)/2

        // create a square bitmap
        // a square is closed, two dimensional shape with 4 equal sides
        return Bitmap.createBitmap(
            this, // source bitmap
            xOffset, // x coordinate of the first pixel in source
            yOffset, // y coordinate of the first pixel in source
            side, // width
            side // height
        ).scale(400, 400)
    }

    private fun Bitmap.toBase64String(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    // https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#accessing-stored-media
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        loadingDialog.open()
        Thread {
            println("OK")
            if (uri !== null) {
                println("URI OK")
                try {
                    var bitmap = if (Build.VERSION.SDK_INT > 27) {
                        // on newer versions of Android, use the new decodeBitmap method
                        val source: ImageDecoder.Source =
                            ImageDecoder.createSource(this.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        // support older versions of Android by using getBitmap
                        MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    }

                    bitmap = bitmap.toSquare()
                    this.runOnUiThread {
                        avatarImageView.setImageBitmap(bitmap)
                        avatarImageView.visibility = View.VISIBLE
                        noAvatarTextView.visibility = View.GONE
                    }

                    base64string = bitmap.toBase64String()
                    println("BASE64: " + base64string!!.length)

                } catch (err: Exception) {
                    err.printStackTrace()
                }
            }
            runOnUiThread { loadingDialog.dismiss() }
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_doctor)

        specialisationService = SpecialisationService()

        nameTextInput = findViewById(R.id.nameTextInput)
        nameTextLayout = findViewById(R.id.nameTextLayout)
        nameTextInput.addTextChangedListener(TextFieldValidation(nameTextInput))
        surnameTextInput = findViewById(R.id.surnameTextInput)
        surnameTextLayout = findViewById(R.id.surnameTextLayout)
        surnameTextInput.addTextChangedListener(TextFieldValidation(surnameTextInput))
        titleTextInput = findViewById(R.id.titleTextInput)
        titleTextLayout = findViewById(R.id.titleTextLayout)
        titleTextInput.addTextChangedListener(TextFieldValidation(titleTextInput))
        emailTextInput = findViewById(R.id.emailTextInput)
        emailTextLayout = findViewById(R.id.emailTextLayout)
        emailTextInput.addTextChangedListener(TextFieldValidation(emailTextInput))
        phoneTextInput = findViewById(R.id.phoneTextInput)
        phoneTextLayout = findViewById(R.id.phoneTextLayout)
        phoneTextInput.addTextChangedListener(TextFieldValidation(phoneTextInput))
        passwordTextInput = findViewById(R.id.passwordTextInput)
        passwordTextLayout = findViewById(R.id.passwordTextLayout)
        passwordTextInput.addTextChangedListener(TextFieldValidation(passwordTextInput))
        specialisationTextInput = findViewById(R.id.specialisationTextView)
        specialisationTextLayout = findViewById(R.id.specialisationTextLayout)
        specialisationTextInput.addTextChangedListener(TextFieldValidation(specialisationTextInput))
        addressTextInput = findViewById(R.id.addressTextInput)
        addressTextLayout = findViewById(R.id.addressTextLayout)
        addressTextInput.addTextChangedListener(TextFieldValidation(addressTextInput))
        cityTextInput = findViewById(R.id.cityTextInput)
        cityTextLayout = findViewById(R.id.cityTextLayout)
        cityTextInput.addTextChangedListener(TextFieldValidation(cityTextInput))
        descriptionTextInput = findViewById(R.id.descriptionTextInput)
        descriptionTextLayout = findViewById(R.id.descriptionTextLayout)
        descriptionTextInput.addTextChangedListener(TextFieldValidation(descriptionTextInput))
        appointmentsLengthTextInput = findViewById(R.id.appointmentsLengthTextInput)
        appointmentsLengthTextLayout = findViewById(R.id.appointmentsLengthTextLayout)
        appointmentsLengthTextInput.addTextChangedListener(TextFieldValidation(appointmentsLengthTextInput))

        // TODO add dynamic work schedules form

        errorAlert = findViewById(R.id.errorAlert)

        avatarImageView = findViewById(R.id.avatarImageView)
        noAvatarTextView = findViewById(R.id.noAvatarTextView)

        specialisationService.getAll(this)
    }

    fun onSubmit(x: View) {
        loadingDialog.open()
        hideError()

        if (isValidForm()) {
            val avatar =
                if (base64string === null) null
                else Avatar(
                    base64string,
                    emailTextInput.text.toString().replace(Regex("[.@]"), ""),
                    "jpeg"
                )
            val rr = RegisterDoctorRequest(
                nameTextInput.text.toString(),
                surnameTextInput.text.toString(),
                titleTextInput.text.toString(),
                emailTextInput.text.toString(),
                phoneTextInput.text.toString(),
                passwordTextInput.text.toString(),
                specialisationService.getByTitle(specialisationTextInput.text.toString())!!.id,
                appointmentsLengthTextInput.text.toString().toInt(),
                addressTextInput.text.toString(),
                cityTextInput.text.toString(),
                descriptionTextInput.text.toString(),
                emptyList(),
                avatar
            )
            AuthService().registerDoctor(rr, this)
        }
    }

    fun onUpload(x: View) {
        resultLauncher.launch("image/*")
    }

    private fun isValidForm(): Boolean {
        return validator.validateRequired(nameTextInput, nameTextLayout, getString(R.string.field_name))
            && validator.validateRequired(surnameTextInput, surnameTextLayout, getString(R.string.field_surname))
            && validator.validateEmail(emailTextInput, emailTextLayout, getString(R.string.field_email))
            && validator.validatePhone(phoneTextInput, phoneTextLayout, getString(R.string.field_phone))
            && validator.validatePassword(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
    }

    private fun hideError() {
        errorAlert.visibility = View.GONE
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.nameTextInput -> {
                    validator.validateRequired(nameTextInput, nameTextLayout, getString(R.string.field_name))
                }
                R.id.surnameTextInput -> {
                    validator.validateRequired(surnameTextInput, surnameTextLayout, getString(R.string.field_surname))
                }
                R.id.titleTextInput -> {
                    validator.validateMaxLength(titleTextInput, titleTextLayout, getString(R.string.field_title), 8)
                }
                R.id.emailTextInput -> {
                    validator.validateEmail(emailTextInput, emailTextLayout, getString(R.string.field_email))
                }
                R.id.phoneTextInput -> {
                    validator.validatePhone(phoneTextInput, phoneTextLayout, getString(R.string.field_phone))
                }
                R.id.passwordTextInput -> {
                    validator.validatePassword(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
                }
                R.id.specialisationTextView -> {
                    validator.validateRequired(specialisationTextInput, specialisationTextLayout, getString(R.string.field_specialisation))
                }
                R.id.addressTextInput -> {
                    validator.validateRequired(addressTextInput, addressTextLayout, getString(R.string.field_address))
                }
                R.id.cityTextInput -> {
                    validator.validateRequired(cityTextInput, cityTextLayout, getString(R.string.field_city))
                }
                R.id.appointmentsLengthTextInput -> {
                    validator.validateRequired(appointmentsLengthTextInput, appointmentsLengthTextLayout, getString(R.string.field_appointments_length))
                        && validator.validateNumber(appointmentsLengthTextInput, appointmentsLengthTextLayout, getString(R.string.field_appointments_length))
                        && validator.validateMin(appointmentsLengthTextInput, appointmentsLengthTextLayout, getString(R.string.field_appointments_length), 5)
                }
            }
        }
        override fun afterTextChanged(p0: Editable?) {}
    }

    override fun showError(error: ApiError) {
        loadingDialog.dismiss()
        errorAlert.visibility = View.VISIBLE
        errorAlert.text = error.message
    }

    fun successfulRegistration() {
        loadingDialog.dismiss()
        val intent = Intent(this, LoginActivity::class.java)
        Toast.makeText(applicationContext, getString(R.string.register_success),Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()
    }

    override fun getAllSpecialisationSuccess(specialisations: Array<Specialisation>) {
        val specialisationItems: List<String> = specialisations.map { s: Specialisation -> s.title }
        val adapter = ArrayAdapter(this, R.layout.list_item, specialisationItems)
        (specialisationTextInput as? AutoCompleteTextView)?.setAdapter(adapter)
    }

}