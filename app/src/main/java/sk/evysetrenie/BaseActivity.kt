package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import sk.evysetrenie.dialogs.LoadingDialog
import sk.evysetrenie.api.AuthState

open class BaseActivity : AppCompatActivity() {

    lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(this)
    }

    fun onClickBack(x: View) {
        this.finish()
    }

    fun logout(isExpired: Boolean = true) {
        AuthState.logout()
        if (isExpired) {
            Toast.makeText(
                applicationContext,
                "Vypršala platnosť Vášho prihlásenia. Prosím, prihláste sa znovu.",
                Toast.LENGTH_LONG
            ).show()
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun checkLoggedIn() {
        if (!AuthState.isLoggedIn()) {
            logout(false)
        }
    }

}