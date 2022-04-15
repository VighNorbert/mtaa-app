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
import sk.evysetrenie.api.*
import sk.evysetrenie.api.model.Avatar
import sk.evysetrenie.api.model.Doctor
import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.WorkSchedule
import sk.evysetrenie.api.model.contracts.requests.RegisterDoctorRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.DoctorsDetailResponse

class MyProfileActivity : MenuActivity(), SpecialisationReader, ProfileEditor, DoctorsDetailReader {

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

    private lateinit var specialisationTextInput: AutoCompleteTextView
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

    override var base64string: String? = null

    private var workSchedulesList: MutableList<WorkSchedule> = ArrayList()
    private lateinit var workSchedulesAdapter: WorkSchedulesAdapter
    private lateinit var workSchedulesRecyclerView: RecyclerView

    private lateinit var specialisationService: SpecialisationService

    private lateinit var imageManager: ImageManager

    private lateinit var me: Doctor

    override fun onBackPressed() { }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkLoggedIn()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_profile)
            me = AuthState.getLoggedInDoctor()
        }
        super.onCreate(savedInstanceState)
        if (AuthState.isLoggedIn()) {
            specialisationService = SpecialisationService()

            nameTextInput = findViewById(R.id.nameTextInput)
            nameTextLayout = findViewById(R.id.nameTextLayout)
            nameTextInput.addTextChangedListener(TextFieldValidation(nameTextInput))
            nameTextInput.setText(me.name)
            surnameTextInput = findViewById(R.id.surnameTextInput)
            surnameTextLayout = findViewById(R.id.surnameTextLayout)
            surnameTextInput.addTextChangedListener(TextFieldValidation(surnameTextInput))
            surnameTextInput.setText(me.surname)
            titleTextInput = findViewById(R.id.titleTextInput)
            titleTextLayout = findViewById(R.id.titleTextLayout)
            titleTextInput.addTextChangedListener(TextFieldValidation(titleTextInput))
            titleTextInput.setText(me.title)
            emailTextInput = findViewById(R.id.emailTextInput)
            emailTextLayout = findViewById(R.id.emailTextLayout)
            emailTextInput.addTextChangedListener(TextFieldValidation(emailTextInput))
            emailTextInput.setText(me.email)
            phoneTextInput = findViewById(R.id.phoneTextInput)
            phoneTextLayout = findViewById(R.id.phoneTextLayout)
            phoneTextInput.addTextChangedListener(TextFieldValidation(phoneTextInput))
            phoneTextInput.setText(me.phone)
            passwordTextInput = findViewById(R.id.passwordTextInput)
            passwordTextLayout = findViewById(R.id.passwordTextLayout)
            passwordTextInput.addTextChangedListener(TextFieldValidation(passwordTextInput))

            specialisationTextInput = findViewById(R.id.specialisationTextView)
            specialisationTextLayout = findViewById(R.id.specialisationTextLayout)
            specialisationTextInput.addTextChangedListener(TextFieldValidation(specialisationTextInput))
            specialisationTextInput.setText(me.specialisation.title, false)
            addressTextInput = findViewById(R.id.addressTextInput)
            addressTextLayout = findViewById(R.id.addressTextLayout)
            addressTextInput.addTextChangedListener(TextFieldValidation(addressTextInput))
            addressTextInput.setText(me.address)
            cityTextInput = findViewById(R.id.cityTextInput)
            cityTextLayout = findViewById(R.id.cityTextLayout)
            cityTextInput.addTextChangedListener(TextFieldValidation(cityTextInput))
            cityTextInput.setText(me.city)
            descriptionTextInput = findViewById(R.id.descriptionTextInput)
            descriptionTextLayout = findViewById(R.id.descriptionTextLayout)
            descriptionTextInput.addTextChangedListener(TextFieldValidation(descriptionTextInput))
            descriptionTextInput.setText(me.description)
            appointmentsLengthTextInput = findViewById(R.id.appointmentsLengthTextInput)
            appointmentsLengthTextLayout = findViewById(R.id.appointmentsLengthTextLayout)
            appointmentsLengthTextInput.addTextChangedListener(TextFieldValidation(appointmentsLengthTextInput))
            appointmentsLengthTextInput.setText(me.appointments_length.toString())

            workSchedulesAdapter = WorkSchedulesAdapter(workSchedulesList, this)
            workSchedulesRecyclerView = findViewById(R.id.workSchedulesRecyclerView)
            workSchedulesRecyclerView.layoutManager = LinearLayoutManager(this)
            workSchedulesRecyclerView.adapter = workSchedulesAdapter

            errorAlert = findViewById(R.id.errorAlert)

            avatarImageView = findViewById(R.id.avatarImageView)
            noAvatarTextView = findViewById(R.id.noAvatarTextView)

            specialisationService.getAll(this, this)

            imageManager = ImageManager(this, avatarImageView, noAvatarTextView)

            DoctorsService().getDetail(me.id!!, this)
        }
    }

    fun addNewWorkSchedule(x : View) {
        workSchedulesAdapter.addItem(WorkSchedule(0, "08:00:00", "16:00:00"))
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
            val password = passwordTextInput.text.toString()
            val rr = RegisterDoctorRequest(
                nameTextInput.text.toString(),
                surnameTextInput.text.toString(),
                titleTextInput.text.toString(),
                emailTextInput.text.toString(),
                phoneTextInput.text.toString(),
                password.ifEmpty { null },
                specialisationService.getByTitle(specialisationTextInput.text.toString())!!.id,
                appointmentsLengthTextInput.text.toString().toInt(),
                addressTextInput.text.toString(),
                cityTextInput.text.toString(),
                descriptionTextInput.text.toString(),
                if (workSchedulesAdapter.changed) workSchedulesList else null,
                avatar
            )
            AuthService().editProfile(rr, this)
        }
    }

    fun onUpload(x: View) {
        imageManager.resultLauncher.launch("image/*")
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

    override fun dataReceived(doctor: DoctorsDetailResponse) {
        doctor.schedules.forEach { ws ->
            workSchedulesList.add(ws)
        }
        workSchedulesAdapter.notifyItemRangeInserted(workSchedulesList.size - doctor.schedules.size, doctor.schedules.size)
    }

    fun successfullyChanged() {
        loadingDialog.dismiss()

        me.name = nameTextInput.text.toString()
        me.surname = surnameTextInput.text.toString()
        me.title = titleTextInput.text.toString()
        me.email = emailTextInput.text.toString()
        me.phone = phoneTextInput.text.toString()
        me.specialisation = specialisationService.getByTitle(specialisationTextInput.text.toString())!!
        me.appointments_length = appointmentsLengthTextInput.text.toString().toInt()
        me.address = addressTextInput.text.toString()
        me.city = cityTextInput.text.toString()
        me.description = descriptionTextInput.text.toString()

        val intent = Intent(this, MyProfileActivity::class.java)
        Toast.makeText(applicationContext, getString(R.string.my_profile_success), Toast.LENGTH_LONG).show()
        startActivity(intent)
        finish()
    }

    override fun getAllSpecialisationSuccess(specialisations: Array<Specialisation>) {
        val specialisationItems: List<String> = specialisations.map { s: Specialisation -> s.title }
        val adapter = ArrayAdapter(this, R.layout.list_item, specialisationItems)
        (specialisationTextInput as? AutoCompleteTextView)?.setAdapter(adapter)
    }

}