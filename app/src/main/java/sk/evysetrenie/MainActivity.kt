package sk.evysetrenie

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.webrtc.Constants

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView

    val db = Firebase.firestore

    private lateinit var startMeeting: Button
    private lateinit var joinMeeting: Button
    private lateinit var meetingIdInput: EditText

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLoggedOut()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_main)
            textView = findViewById(R.id.text)

            val liu = AuthState.getLoggedIn()!!
            textView.text = "Prihlásený používateľ: " + liu.name + " " + liu.surname

            startMeeting = findViewById(R.id.start_meeting)
            joinMeeting = findViewById(R.id.join_meeting)
            meetingIdInput = findViewById(R.id.meeting_id)

            Constants.isIntiatedNow = true
            Constants.isCallEnded = true
            startMeeting.setOnClickListener {
                if (meetingIdInput.text.toString().trim().isEmpty())
                    meetingIdInput.error = "Please enter meeting id"
                else {
                    db.collection("calls")
                        .document(meetingIdInput.text.toString())
                        .get()
                        .addOnSuccessListener {
                            if (it["type"]=="OFFER" || it["type"]=="ANSWER" || it["type"]=="END_CALL") {
                                meetingIdInput.error = "Please enter new meeting ID"
                            } else {
                                val intent = Intent(this@MainActivity, RTCActivity::class.java)
                                intent.putExtra("meetingID",meetingIdInput.text.toString())
                                intent.putExtra("isJoin",false)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener {
                            meetingIdInput.error = "Please enter new meeting ID"
                        }
                }
            }
            joinMeeting.setOnClickListener {
                if (meetingIdInput.text.toString().trim().isEmpty())
                    meetingIdInput.error = "Please enter meeting id"
                else {
                    val intent = Intent(this@MainActivity, RTCActivity::class.java)
                    intent.putExtra("meetingID",meetingIdInput.text.toString())
                    intent.putExtra("isJoin",true)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onBackPressed() { }

    private fun checkLoggedOut() {
        if (!AuthState.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}