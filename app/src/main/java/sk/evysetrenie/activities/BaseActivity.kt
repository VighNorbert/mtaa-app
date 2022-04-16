package sk.evysetrenie.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
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

    fun checkLoggedIn() {
        if (!AuthState.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}