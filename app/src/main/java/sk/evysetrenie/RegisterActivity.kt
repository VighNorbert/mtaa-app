package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.view.View

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun onClickRegisterDoctor(x: View) {
    }

    fun onClickRegisterPatient(x: View) {
        val intent = Intent(this, RegisterPatientActivity::class.java)
        startActivity(intent)
    }
}