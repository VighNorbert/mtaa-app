package sk.evysetrenie

import android.os.Bundle
import sk.evysetrenie.api.AuthState

class AppointmentsActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        checkLoggedIn()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_appointments)
        }
        super.onCreate(savedInstanceState)

    }

    override fun onBackPressed() { }

}