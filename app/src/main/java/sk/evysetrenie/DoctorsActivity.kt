package sk.evysetrenie

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.DoctorsService
import sk.evysetrenie.api.model.Doctor
import sk.evysetrenie.api.model.contracts.requests.DoctorsRequest
import sk.evysetrenie.api.model.contracts.responses.DoctorsResponse

class DoctorsActivity : BaseActivity() {

    private var name: String? = null
    private var specialisation: Int? = null
    private var city: String? = null
    private var only_favourites: Boolean = false
    private var page: Int = 1
    private var per_page: Int = 10

    private lateinit var doctorsAdapter: DoctorsAdapter

    private lateinit var doctorName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog.open()
        checkLoggedOut()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_doctors)
        }
        val dr = DoctorsRequest(
            this.name,
            this.specialisation,
            this.city,
            this.only_favourites,
            this.page,
            this.per_page
        )
        DoctorsService().getCollection(dr, this)
    }

    override fun onBackPressed() { }

    private fun checkLoggedOut() {
        if (!AuthState.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun dataReceived(doctorsResponse: List<DoctorsResponse>) {
        doctorsAdapter = DoctorsAdapter()
        loadingDialog.dismiss()
        doctorName.text = doctorsResponse.get(0).name
    }
}