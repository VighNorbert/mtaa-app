package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import sk.evysetrenie.api.AuthService
import sk.evysetrenie.api.Validator
import sk.evysetrenie.api.model.contracts.requests.RegisterRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError

class RegisterPatientActivity : ReturningActivity() {

    private lateinit var nameTextInput: TextInputEditText
    private lateinit var nameTextLayout: TextInputLayout

    private lateinit var surnameTextInput: TextInputEditText
    private lateinit var surnameTextLayout: TextInputLayout

    private lateinit var emailTextInput: TextInputEditText
    private lateinit var emailTextLayout: TextInputLayout

    private lateinit var phoneTextInput: TextInputEditText
    private lateinit var phoneTextLayout: TextInputLayout

    private lateinit var passwordTextInput: TextInputEditText
    private lateinit var passwordTextLayout: TextInputLayout

    private lateinit var errorAlert: TextView

    private val validator: Validator = Validator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_patient)

        nameTextInput = findViewById(R.id.nameTextInput)
        nameTextLayout = findViewById(R.id.nameTextLayout)
        nameTextInput.addTextChangedListener(TextFieldValidation(nameTextInput))
        surnameTextInput = findViewById(R.id.surnameTextInput)
        surnameTextLayout = findViewById(R.id.surnameTextLayout)
        surnameTextInput.addTextChangedListener(TextFieldValidation(surnameTextInput))
        emailTextInput = findViewById(R.id.emailTextInput)
        emailTextLayout = findViewById(R.id.emailTextLayout)
        emailTextInput.addTextChangedListener(TextFieldValidation(emailTextInput))
        phoneTextInput = findViewById(R.id.phoneTextInput)
        phoneTextLayout = findViewById(R.id.phoneTextLayout)
        phoneTextInput.addTextChangedListener(TextFieldValidation(phoneTextInput))
        passwordTextInput = findViewById(R.id.passwordTextInput)
        passwordTextLayout = findViewById(R.id.passwordTextLayout)
        passwordTextInput.addTextChangedListener(TextFieldValidation(passwordTextInput))

        errorAlert = findViewById(R.id.errorAlert)
    }

    fun onSubmit(x: View) {
        loadingDialog.open()
        hideError()

        if (isValidForm()) {
            val rr = RegisterRequest(
                nameTextInput.text.toString(),
                surnameTextInput.text.toString(),
                emailTextInput.text.toString(),
                phoneTextInput.text.toString(),
                passwordTextInput.text.toString(),
            )
            AuthService().register(rr, this)
        }
    }

    private fun isValidForm(): Boolean {
        return validator.validateRequired(nameTextInput, nameTextLayout, getString(R.string.field_name))
            && validator.validateRequired(surnameTextInput, surnameTextLayout, getString(R.string.field_surname))
            && validator.validateEmail(emailTextInput, emailTextLayout, getString(R.string.field_email))
            && validator.validatePhone(phoneTextInput, phoneTextLayout, getString(R.string.field_phone))
            && validator.validatePassword(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
    }

    fun hideError() {
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
                R.id.emailTextInput -> {
                    validator.validateEmail(emailTextInput, emailTextLayout, getString(R.string.field_email))
                }
                R.id.phoneTextInput -> {
                    validator.validatePhone(phoneTextInput, phoneTextLayout, getString(R.string.field_phone))
                }
                R.id.passwordTextInput -> {
                    validator.validatePassword(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
                }
            }
        }
        override fun afterTextChanged(p0: Editable?) {}
    }

    fun showError(error: ApiError) {
        loadingDialog.dismiss()
        errorAlert.visibility = View.VISIBLE
        errorAlert.text = error.message
    }

    fun successfulRegistration(userId: Int) {
        loadingDialog.dismiss()
        val intent = Intent(this, LoginActivity::class.java)
        Toast.makeText(applicationContext, getString(R.string.register_success),Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()
    }

}