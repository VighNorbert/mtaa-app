package sk.evysetrenie

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import sk.evysetrenie.api.AuthState

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLoggedOut()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_main)
            textView = findViewById(R.id.text)

            val liu = AuthState.getLoggedIn()!!
            textView.text = "Prihlásený používateľ: " + liu.name + " " + liu.surname
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