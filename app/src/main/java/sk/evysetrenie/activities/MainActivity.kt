package sk.evysetrenie.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.evysetrenie.R
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.webrtc.Constants

class MainActivity : MenuActivity() {
    val db = Firebase.firestore

    private lateinit var startMeeting: Button
    private lateinit var meetingIdInput: EditText

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        checkLoggedIn()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_main)
        }
        super.onCreate(savedInstanceState)

        if (AuthState.isLoggedIn()) {
            startMeeting = findViewById(R.id.start_meeting)
            meetingIdInput = findViewById(R.id.meeting_id)

            Constants.isIntiatedNow = true
            Constants.isCallEnded = true
            startMeeting.setOnClickListener {
                onStartMeeting()
            }
        }
    }

    private fun onStartMeeting() {
        if (meetingIdInput.text.toString().trim().isEmpty())
            meetingIdInput.error = "Please enter meeting id"
        else {
            db.collection("calls")
                .document(meetingIdInput.text.toString())
                .get()
                .addOnSuccessListener {
                    if (it["type"]=="OFFER" || it["type"]=="ANSWER" || it["type"]=="END_CALL") {
                        onJoinMeeting()
                    } else {
                        val intent = Intent(this@MainActivity, RTCActivity::class.java)
                        intent.putExtra("meetingID",meetingIdInput.text.toString())
                        intent.putExtra("isJoin",false)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    onJoinMeeting()
                }
        }
    }

    private fun onJoinMeeting() {
        if (meetingIdInput.text.toString().trim().isEmpty())
            meetingIdInput.error = "Please enter meeting id"
        else {
            val intent = Intent(this@MainActivity, RTCActivity::class.java)
            intent.putExtra("meetingID",meetingIdInput.text.toString())
            intent.putExtra("isJoin",true)
            startActivity(intent)
        }
    }

    override fun onBackPressed() { }
}