package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.view.View

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onClickRegister(x: View) {
        println("clicked")
        val intent = Intent(this, RegisterActivity::class.java)
//        .apply {
//            putExtra(EXTRA_MESSAGE, message)
//        }
        startActivity(intent)
    }

}