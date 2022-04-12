package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View

class RegisterActivity : ReturningActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun onClickRegisterDoctor(x: View) {
        val intent = Intent(this, RegisterDoctorActivity::class.java)
        startActivity(intent)
    }

    fun onClickRegisterPatient(x: View) {
        val intent = Intent(this, RegisterPatientActivity::class.java)
        startActivity(intent)
    }
}