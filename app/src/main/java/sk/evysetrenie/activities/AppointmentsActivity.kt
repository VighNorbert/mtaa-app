package sk.evysetrenie.activities

import android.os.Bundle
import sk.evysetrenie.R
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