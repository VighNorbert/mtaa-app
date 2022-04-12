package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import sk.evysetrenie.api.AuthService
import sk.evysetrenie.api.model.contracts.requests.LoginRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError

class LoginActivity : BaseActivity() {

    private lateinit var emailTextInput: TextInputEditText
    private lateinit var passwordTextInput: TextInputEditText
    private lateinit var errorAlert: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailTextInput = findViewById(R.id.emailTextInput)
        passwordTextInput = findViewById(R.id.passwordTextInput)
        errorAlert = findViewById(R.id.errorAlert)
    }

    override fun onBackPressed() { }

    fun onClickRegister(x: View) {
        println("clicked")
        val intent = Intent(this, RegisterActivity::class.java)
//        .apply {
//            putExtra(EXTRA_MESSAGE, message)
//        }
        startActivity(intent)
    }

    fun successfulLogin() {
        loadingDialog.dismiss()
        val intent = Intent(this, DoctorsActivity::class.java)
        startActivity(intent)
    }

    fun onSubmit(x: View) {
        loadingDialog.open()
        val lr = LoginRequest(
            emailTextInput.text.toString(),
            passwordTextInput.text.toString()
        )
        AuthService().authenticate(lr, this)
    }

    fun showError(error: ApiError) {
        loadingDialog.dismiss()
        errorAlert.visibility = View.VISIBLE
        errorAlert.text = error.message
    }

}