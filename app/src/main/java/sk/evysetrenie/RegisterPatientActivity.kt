package sk.evysetrenie

import android.os.Bundle
import android.view.View
import com.google.android.material.textfield.TextInputEditText

class RegisterPatientActivity : BaseActivity() {

    private lateinit var nameTextInput: TextInputEditText
    private lateinit var surnameTextInput: TextInputEditText
    private lateinit var emailTextInput: TextInputEditText
    private lateinit var phoneTextInput: TextInputEditText
    private lateinit var passwordTextInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_patient)

        nameTextInput = findViewById(R.id.nameTextInput)
        surnameTextInput = findViewById(R.id.surnameTextInput)
        emailTextInput = findViewById(R.id.emailTextInput)
        phoneTextInput = findViewById(R.id.phoneTextInput)
        passwordTextInput = findViewById(R.id.passwordTextInput)
    }

    fun onSubmit(x: View) {
        val allEnvs = System.getenv()
        allEnvs.forEach { (k, v) -> println("$k => $v") }
        println("name " + nameTextInput.text)
        println("surname " + surnameTextInput.text)
        println("email " + emailTextInput.text)
        println("phone " + phoneTextInput.text)
        println("pwd " + passwordTextInput.text)

    }

}