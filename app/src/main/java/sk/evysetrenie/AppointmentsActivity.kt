package sk.evysetrenie

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.adapters.AppointmentsAdapter
import sk.evysetrenie.api.AppointmentsService
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.model.WorkSchedule
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.AppointmentResponse

class AppointmentsActivity : MenuActivity() {

    private lateinit var appointmentsLayoutManager: LinearLayoutManager
    private lateinit var appointmentsAdapter: AppointmentsAdapter
    private lateinit var appointmentsNoResultTextView: TextView
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var appointmentsProgressBar: ProgressBar

    private var appointmentsList: MutableList<AppointmentResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        checkLoggedIn()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_appointments)
            appointmentsLayoutManager = LinearLayoutManager(this)
            appointmentsNoResultTextView = findViewById(R.id.appointmentsNoResultsTextView)
            appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView)
            appointmentsRecyclerView.layoutManager = appointmentsLayoutManager
            appointmentsProgressBar = findViewById(R.id.appointmentsProgressBar)
        }
        super.onCreate(savedInstanceState)
        if (AuthState.isLoggedIn()) {
            AppointmentsService().getCollection(null, this)
        }
    }

    override fun onBackPressed() { }

    fun dataReceived(appointments: List<AppointmentResponse>) {
        val sortedappointments = appointments.sortedWith(compareBy<AppointmentResponse> { it.date }.thenBy { it.time_from })
        appointmentsList.addAll(sortedappointments)
        if (appointmentsList.isEmpty()) {
            appointmentsNoResultTextView.visibility = View.VISIBLE
        } else {
            appointmentsNoResultTextView.visibility = View.GONE
        }
        if (this::appointmentsAdapter.isInitialized) {
            appointmentsAdapter.notifyItemRangeInserted(appointmentsList.size - appointments.size, appointmentsList.size)
        }
        else {
            appointmentsAdapter = AppointmentsAdapter(appointmentsList, this)
            appointmentsRecyclerView.adapter = appointmentsAdapter
        }
        appointmentsProgressBar.visibility = View.GONE
    }

    fun showError(error: ApiError) {
        loadingDialog.dismiss()
        appointmentsProgressBar.visibility = View.GONE
        Toast.makeText(this.applicationContext, error.message, Toast.LENGTH_SHORT).show()
    }

    fun removeAppointment(appointmentResponse: AppointmentResponse) {
        loadingDialog.open()

        AppointmentsService().remove(appointmentResponse.doctor.id!!, appointmentResponse.id, this)
    }

    fun successfulRemoval() {
        loadingDialog.dismiss()
        appointmentsAdapter.removeSuccess()

        if (appointmentsList.isEmpty()) {
            appointmentsNoResultTextView.visibility = View.VISIBLE
        } else {
            appointmentsNoResultTextView.visibility = View.GONE
        }
        Toast.makeText(applicationContext, "Termín vyšetrenia bol zrušený", Toast.LENGTH_SHORT).show()
    }
}