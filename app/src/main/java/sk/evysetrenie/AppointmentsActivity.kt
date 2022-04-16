package sk.evysetrenie

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.adapters.AppointmentsAdapter
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.AppointmentResponse

class AppointmentsActivity : MenuActivity() {

    private lateinit var appointmentsLayoutManager: LinearLayoutManager
    private lateinit var appointmentsAdapter: AppointmentsAdapter
    private lateinit var appointmentsNoResultTextView: TextView
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var appointmentsProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        checkLoggedIn()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_appointments)
            appointmentsLayoutManager = LinearLayoutManager(this)
            appointmentsNoResultTextView = findViewById(R.id.appointmentsNoResultsTextView)
            appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView)
            appointmentsProgressBar = findViewById(R.id.appointmentsProgressBar)
        }
        super.onCreate(savedInstanceState)

        // TODO adapter
    }

    override fun onBackPressed() { }

    fun dataReceived(appointments: List<AppointmentResponse>) {

    }

    fun showError(error: ApiError) {
        appointmentsProgressBar.visibility = View.GONE
        Toast.makeText(this.applicationContext, error.message, Toast.LENGTH_SHORT).show()
    }
}