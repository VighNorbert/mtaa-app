package sk.evysetrenie.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.AppointmentsActivity
import sk.evysetrenie.R
import sk.evysetrenie.api.model.contracts.responses.DoctorsResponse

class AppointmentsAdapter(private val appointmentsList: List<DoctorsResponse>, val activity: AppointmentsActivity) : RecyclerView.Adapter<AppointmentsAdapter.DoctorsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsHolder {
        return DoctorsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_doctors, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorsHolder, position: Int) {
        val doctor = appointmentsList[position]
        val name = doctor.title + " " + doctor.name + " " + doctor.surname
        val spannableString = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldSpan, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.doctorNameTextView.text = spannableString
        holder.doctorSpecialisationTextView.text = doctor.specialisation.title
        if (doctor.is_favourite) {
            holder.doctorStar.setImageResource(R.drawable.star_filled)
        }
        else {
            holder.doctorStar.setImageResource(R.drawable.star_unfilled)
        }
    }

    override fun getItemCount() = appointmentsList.size

    class DoctorsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorLinearLayout: LinearLayout = view.findViewById(R.id.doctorLinearLayout)
        val doctorNameTextView: TextView = view.findViewById(R.id.doctorNameTextView)
        val doctorSpecialisationTextView: TextView = view.findViewById(R.id.doctorSpecialisationTextView)
        val doctorStar: ImageView = view.findViewById(R.id.doctorStar)
    }
}