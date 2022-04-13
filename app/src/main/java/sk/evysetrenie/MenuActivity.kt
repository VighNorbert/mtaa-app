package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class MenuActivity : BaseActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.general_open, R.string.general_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_doctors ->
                    startActivity(
                        Intent(applicationContext, DoctorsActivity::class.java)
                    )
                R.id.nav_appointments ->
                    startActivity(
                        Intent(applicationContext, AppointmentsActivity::class.java)
                    )
                R.id.nav_profile ->
                    startActivity(
                        Intent(applicationContext, MyProfileActivity::class.java)
                    )
                R.id.nav_webrtc ->
                    startActivity(
                        Intent(applicationContext, MainActivity::class.java)
                    )
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}