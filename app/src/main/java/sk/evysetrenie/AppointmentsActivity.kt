package sk.evysetrenie

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.evysetrenie.adapters.AppointmentsAdapter
import sk.evysetrenie.api.AppointmentsService
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.AppointmentResponse
import sk.evysetrenie.webrtc.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AppointmentsActivity : MenuActivity() {

    val db = Firebase.firestore

    private lateinit var appointmentsLayoutManager: LinearLayoutManager
    private lateinit var appointmentsAdapter: AppointmentsAdapter
    private lateinit var appointmentsNoResultTextView: TextView
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var appointmentsProgressBar: ProgressBar

    var meetingId : String = ""
    var meetingIdBase : String = ""
    var meetingIdModifier : Int = 0

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

            Constants.isIntiatedNow = true
            Constants.isCallEnded = true
        }
    }

    override fun onBackPressed() { }

    @SuppressLint("SimpleDateFormat")
    fun dataReceived(appointments: List<AppointmentResponse>) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val currentTime = SimpleDateFormat("HH:mm").format(Date())
        val sortedappointments = appointments
            .filter { it.date > currentDate || (it.date == currentDate && it.time_to > currentTime) }
            .sortedWith(compareBy<AppointmentResponse> { it.date }.thenBy { it.time_from })
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

    fun startMeeting(meetingId: Int) {
        this.meetingId = meetingId.toString()
        this.meetingIdBase = this.meetingId
        meetingIdModifier = 1
        onStartMeeting()
    }

    private fun onStartMeeting() {
        db.collection("calls")
            .document(meetingId)
            .get()
            .addOnSuccessListener {
                if (it["type"]=="OFFER" || it["type"]=="ANSWER") {
                    onJoinMeeting()
                } else if (it["type"]=="END_CALL") {
                    meetingIdModifier++
                    meetingId = "$meetingIdBase-$meetingIdModifier"
                    onStartMeeting()
                } else {
                    val intent = Intent(this@AppointmentsActivity, RTCActivity::class.java)
                    intent.putExtra("meetingID", meetingId)
                    intent.putExtra("isJoin",false)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                onJoinMeeting()
            }
    }

    private fun onJoinMeeting() {
        val intent = Intent(this@AppointmentsActivity, RTCActivity::class.java)
        intent.putExtra("meetingID", meetingId)
        intent.putExtra("isJoin",true)
        startActivity(intent)
    }
}