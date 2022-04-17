package sk.evysetrenie

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.adapters.DoctorsAdapter
import sk.evysetrenie.api.interfaces.FavouriteSetter
import sk.evysetrenie.api.interfaces.SpecialisationReader
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.DoctorsService
import sk.evysetrenie.api.SpecialisationService
import sk.evysetrenie.api.model.Specialisation
import sk.evysetrenie.api.model.contracts.requests.DoctorsRequest
import sk.evysetrenie.api.model.contracts.responses.ApiError
import sk.evysetrenie.api.model.contracts.responses.DoctorsResponse

open class DoctorsActivity : MenuActivity(), SpecialisationReader, FavouriteSetter {

    private var name: String? = null
    private var specialisation: Int? = null
    private var city: String? = null
    private var only_favourites: Boolean = false
    private var page: Int = 1
    private var per_page: Int = 10

    private var loading = false

    private lateinit var doctorsLayoutManager: LinearLayoutManager
    private lateinit var doctorsAdapter: DoctorsAdapter
    private lateinit var doctorsNoResultTextView: TextView
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
        if (AuthState.isLoggedIn()) {
            doctorsLayoutManager = LinearLayoutManager(this)
            doctorsNoResultTextView = findViewById(R.id.doctorsNoResultsTextView)
            doctorsRecyclerView = findViewById(R.id.doctorsRecyclerView)
            doctorsProgressBar = findViewById(R.id.doctorsProgressBar)
            doctorsFilterLayout = findViewById(R.id.detailAppointmentLayout)

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
        if (doctorsList.isEmpty()) {
            doctorsNoResultTextView.visibility = View.VISIBLE
        }
        else {
            doctorsNoResultTextView.visibility = View.GONE
        }
        doctorsRecyclerView.layoutManager = doctorsLayoutManager
        if (this::doctorsAdapter.isInitialized) {
            doctorsAdapter.notifyItemRangeInserted(doctorsList.size - doctorsResponseCollection.size, doctorsResponseCollection.size)
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

    @SuppressLint("NotifyDataSetChanged")
    fun onFiltersSubmit(x: View) {
        if (doctorNameInputText.text.isNotEmpty()) {
            name = doctorNameInputText.text.toString()
        } else {
            name = null
        }
        if (doctorSpecialisationInputText.text.isNotEmpty()) {
            specialisation = specialisationService.getByTitle(doctorSpecialisationInputText.text.toString())!!.id
        } else {
            specialisation = null
        }
        if (doctorCityInputText.text.isNotEmpty()) {
            city = doctorCityInputText.text.toString()
        } else {
            city = null
        }
        only_favourites = doctorOnlyFavouritesCheckBox.isChecked
        page = 1
        doctorsList.clear()
        doctorsAdapter.notifyDataSetChanged()
        x.hideKeyboard()
        getDoctors()
        doctorsFilterLayout.visibility = View.GONE
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun getAllSpecialisationSuccess(specialisations: Array<Specialisation>) {
        val specialisationItems: List<String> = specialisations.map { s: Specialisation -> s.title }
        val adapter = ArrayAdapter(this, R.layout.list_item, specialisationItems)
        (doctorSpecialisationInputText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    fun getDoctorDetail(id: Int) {
        val intent = Intent(this, DoctorsDetailActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun showError(error: ApiError) {
        doctorsProgressBar.visibility = View.GONE;
        Toast.makeText(this.applicationContext, error.message, Toast.LENGTH_SHORT).show()
    }
}