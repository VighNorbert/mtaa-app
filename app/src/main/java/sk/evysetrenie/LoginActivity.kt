package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import sk.evysetrenie.api.AuthService
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.Validator
import sk.evysetrenie.api.model.contracts.requests.LoginRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError

class LoginActivity : BaseActivity() {

    private lateinit var emailTextInput: TextInputEditText
    private lateinit var emailTextLayout: TextInputLayout

    private lateinit var passwordTextInput: TextInputEditText
    private lateinit var passwordTextLayout: TextInputLayout

    private lateinit var errorAlert: TextView

    private val validator: Validator = Validator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthState.isLoggedIn()) {
            val intent = Intent(this, DoctorsActivity::class.java)
            startActivity(intent)
        }

        setContentView(R.layout.activity_login)

        emailTextInput = findViewById(R.id.emailTextInput)
        emailTextLayout = findViewById(R.id.emailTextLayout)
        emailTextInput.addTextChangedListener(TextFieldValidation(emailTextInput))
        passwordTextInput = findViewById(R.id.passwordTextInput)
        passwordTextLayout = findViewById(R.id.passwordTextLayout)
        passwordTextInput.addTextChangedListener(TextFieldValidation(passwordTextInput))
        errorAlert = findViewById(R.id.errorAlert)
    }

    override fun onBackPressed() { }

    fun onClickRegister(x: View) {
        println("clicked")
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun successfulLogin() {
        loadingDialog.dismiss()
        val intent = Intent(this, DoctorsActivity::class.java)
        startActivity(intent)
    }

    fun onSubmit(x: View) {
        loadingDialog.open()
        hideError()

        if (isValidForm()) {
            val lr = LoginRequest(
                emailTextInput.text.toString(),
                passwordTextInput.text.toString()
            )
            AuthService().authenticate(lr, this)
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun isValidForm(): Boolean {
        return validator.validateEmail(emailTextInput, emailTextLayout, getString(R.string.field_email))
            && validator.validateRequired(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.emailTextInput -> {
                    validator.validateEmail(emailTextInput, emailTextLayout, getString(R.string.field_email))
                }
                R.id.passwordTextInput -> {
                    validator.validateRequired(passwordTextInput, passwordTextLayout, getString(R.string.field_password))
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

    private fun hideError() {
        errorAlert.visibility = View.GONE
    }

}