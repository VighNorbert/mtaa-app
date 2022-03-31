package sk.evysetrenie

import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun onClickBack(x: View) {
        this.finish()
    }
}