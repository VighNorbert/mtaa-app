package sk.evysetrenie

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.DoctorsService
import sk.evysetrenie.api.SpecialisationService
import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.contracts.requests.DoctorsRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.DoctorsResponse

class DoctorsActivity : MenuActivity(), SpecialisationReader {

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
    private lateinit var doctorsFilterLayout: LinearLayout

    private lateinit var doctorNameInputText: TextView
    private lateinit var doctorSpecialisationInputText: TextView
    private lateinit var doctorCityInputText: TextView
    private lateinit var doctorOnlyFavouritesCheckBox: CheckBox

    private lateinit var specialisationService: SpecialisationService

    private var doctorsList: MutableList<DoctorsResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        checkLoggedIn()
        if (AuthState.isLoggedIn()) {
            setContentView(R.layout.activity_doctors)
        }
        super.onCreate(savedInstanceState)

        doctorsLayoutManager = LinearLayoutManager(this)
        doctorsRecyclerView = findViewById(R.id.doctorsRecyclerView)
        doctorsProgressBar = findViewById(R.id.doctorsProgressBar)
        doctorsFilterLayout = findViewById(R.id.doctorsFilterLayout)

        doctorNameInputText = findViewById(R.id.doctorNameInputText)
        doctorSpecialisationInputText = findViewById(R.id.doctorSpecialisationInputText)
        doctorCityInputText = findViewById(R.id.doctorCityInputText)
        doctorOnlyFavouritesCheckBox = findViewById(R.id.doctorOnlyFavouritesCheckBox)

        specialisationService = SpecialisationService()

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
        specialisationService.getAll(this, this)
    }

    override fun onBackPressed() { }

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
            doctorsAdapter = DoctorsAdapter(doctorsList, this)
            doctorsRecyclerView.adapter = doctorsAdapter
        }
        doctorsProgressBar.visibility = View.GONE
        loading = false
    }

    fun onFiltersClick(x: View) {
        if (doctorsFilterLayout.isVisible) {
            doctorsFilterLayout.visibility = View.GONE
        }
        else {
            doctorsFilterLayout.visibility = View.VISIBLE
        }
    }

    fun onFiltersSubmit(x: View) {
        loadingDialog.open()
        if (doctorNameInputText.text.isNotEmpty()) {
            name = doctorNameInputText.text.toString()
        }
        if (doctorSpecialisationInputText.text.isNotEmpty()) {
            specialisation = specialisationService.getByTitle(doctorSpecialisationInputText.text.toString())!!.id
        }
        if (doctorCityInputText.text.isNotEmpty()) {
            city = doctorCityInputText.text.toString()
        }
        only_favourites = doctorOnlyFavouritesCheckBox.isChecked
        page = 1
        doctorsList.clear()
        getDoctors()
        doctorsFilterLayout.visibility = View.GONE
        loadingDialog.dismiss()
    }

    override fun getAllSpecialisationSuccess(specialisations: Array<Specialisation>) {
        val specialisationItems: List<String> = specialisations.map { s: Specialisation -> s.title }
        val adapter = ArrayAdapter(this, R.layout.list_item, specialisationItems)
        (doctorSpecialisationInputText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    override fun showError(error: ApiError) {
        doctorsProgressBar.visibility = View.GONE;
        Toast.makeText(this.applicationContext, error.message, Toast.LENGTH_SHORT).show()
    }
}