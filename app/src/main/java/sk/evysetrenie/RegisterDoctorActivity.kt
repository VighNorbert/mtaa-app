package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import sk.evysetrenie.api.AuthService
import sk.evysetrenie.api.SpecialisationService
import sk.evysetrenie.api.Validator
import sk.evysetrenie.api.model.Avatar
import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.WorkSchedule
import sk.evysetrenie.api.model.contracts.requests.RegisterDoctorRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError


class RegisterDoctorActivity : ReturningActivity(), SpecialisationReader, ProfileEditor {

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
    private lateinit var workSchedulesRequiredErrorTextView: TextView

    private lateinit var avatarImageView: ImageView
    private lateinit var noAvatarTextView: TextView

    private val validator: Validator = Validator()

    override var base64string : String? = null

    private var workSchedulesList: MutableList<WorkSchedule> = ArrayList()
    private lateinit var workSchedulesAdapter: WorkSchedulesAdapter
    private lateinit var workSchedulesRecyclerView: RecyclerView

    private lateinit var specialisationService: SpecialisationService

    private lateinit var imageManager : ImageManager

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

        workSchedulesAdapter = WorkSchedulesAdapter(workSchedulesList, this)
        workSchedulesRecyclerView = findViewById(R.id.workSchedulesRecyclerView)
        workSchedulesRecyclerView.layoutManager = LinearLayoutManager(this)
        workSchedulesRecyclerView.adapter = workSchedulesAdapter

        errorAlert = findViewById(R.id.errorAlert)
        workSchedulesRequiredErrorTextView = findViewById(R.id.workSchedulesRequiredErrorTextView)

        avatarImageView = findViewById(R.id.avatarImageView)
        noAvatarTextView = findViewById(R.id.noAvatarTextView)

        specialisationService.getAll(this, this)

        imageManager = ImageManager(this, avatarImageView, noAvatarTextView)
    }

    fun addNewWorkSchedule(x : View) {
        workSchedulesAdapter.addItem(WorkSchedule(1, "08:00:00", "16:00:00"))
        workSchedulesRequiredErrorTextView.visibility = View.GONE
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
                workSchedulesList,
                avatar
            )
            AuthService().registerDoctor(rr, this)
        } else {
            loadingDialog.dismiss()
        }
    }

    fun onUpload(x: View) {
        imageManager.resultLauncher.launch("image/*")
    }

    private fun isValidForm(): Boolean {
        return validator.validateRequired(nameTextInput, nameTextLayout, getString(R.string.field_name))
            && validator.validateRequired(surnameTextInput, surnameTextLayout, getString(R.string.field_surname))
            && validator.validateMaxLength(titleTextInput, titleTextLayout, getString(R.string.field_title), 8)
            && validator.validateEmail(emailTextInput, emailTextLayout, getString(R.string.field_email))
            && validator.validatePhone(phoneTextInput, phoneTextLayout, getString(R.string.field_phone))
            && validator.validateRequired(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
            && validator.validatePassword(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
            && validator.validateRequired(specialisationTextInput, specialisationTextLayout, getString(R.string.field_specialisation))
            && validator.validateRequired(addressTextInput, addressTextLayout, getString(R.string.field_address))
            && validator.validateRequired(cityTextInput, cityTextLayout, getString(R.string.field_city))
            && validator.validateRequired(appointmentsLengthTextInput, appointmentsLengthTextLayout, getString(R.string.field_appointments_length))
            && validator.validateNumber(appointmentsLengthTextInput, appointmentsLengthTextLayout, getString(R.string.field_appointments_length))
            && validator.validateMin(appointmentsLengthTextInput, appointmentsLengthTextLayout, getString(R.string.field_appointments_length), 5)
            && validator.validateArrayRequired(workSchedulesList, workSchedulesRequiredErrorTextView)
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
                    validator.validateRequired(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
                    &&
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