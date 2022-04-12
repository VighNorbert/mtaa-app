package sk.evysetrenie

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.DoctorsService
import sk.evysetrenie.api.model.contracts.requests.DoctorsRequest
import sk.evysetrenie.api.model.contracts.responses.DoctorsResponse

class DoctorsActivity : MenuActivity() {

    private var name: String? = null
    private var specialisation: Int? = null
    private var city: String? = null
    private var only_favourites: Boolean = false
    private var page: Int = 1
    private var per_page: Int = 10

    private var loading = false

    private lateinit var doctorsLayoutManager: LinearLayoutManager
    private lateinit var doctorsAdapter: DoctorsAdapter
    private lateinit var doctorsRecyclerView: RecyclerView
    private lateinit var doctorsProgressBar: ProgressBar

    private var doctorsList: MutableList<DoctorsResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        checkLoggedOut()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_doctors)
        }
        super.onCreate(savedInstanceState)

        doctorsLayoutManager = LinearLayoutManager(this)
        doctorsRecyclerView = findViewById(R.id.doctorsRecyclerView)
        doctorsProgressBar = findViewById(R.id.doctorsProgressBar)

        getDoctors()
        doctorsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = doctorsLayoutManager.childCount
                    val pastVisibleItem = doctorsLayoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = doctorsAdapter.itemCount

                    if (!loading) {
                        if (visibleItemCount + pastVisibleItem >= total) {
                            page++
                            getDoctors()
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun onBackPressed() { }

    private fun checkLoggedOut() {
        if (!AuthState.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun getDoctors() {
        doctorsProgressBar.visibility = View.VISIBLE
        loading = true
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

    @SuppressLint("NotifyDataSetChanged")
    fun dataReceived(doctorsResponseCollection: List<DoctorsResponse>) {
        doctorsList.addAll(doctorsResponseCollection)
        doctorsRecyclerView.layoutManager = doctorsLayoutManager
        if (this::doctorsAdapter.isInitialized) {
            doctorsAdapter.notifyDataSetChanged()
        }
        else {
            doctorsAdapter = DoctorsAdapter(doctorsList)
            doctorsRecyclerView.adapter = doctorsAdapter
        }
        doctorsProgressBar.visibility = View.GONE
        loading = false
    }

    fun addDoctorToFavourites(name: String) {
        println("Added $name")
    }
}