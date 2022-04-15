package sk.evysetrenie

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.api.DoctorsService
import sk.evysetrenie.api.model.contracts.responses.DoctorsResponse

class DoctorsAdapter(val doctorsList: List<DoctorsResponse>, val activity: DoctorsActivity) : RecyclerView.Adapter<DoctorsAdapter.DoctorsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsHolder {
        return DoctorsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_doctors, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorsHolder, position: Int) {
        val doctor = doctorsList[position]
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
        holder.doctorNameTextView.setOnClickListener{
            activity.getDoctorDetail(doctor.id)
        }
        holder.doctorStar.setOnClickListener{
            if (doctor.is_favourite) {
                holder.doctorStar.setImageResource(R.drawable.star_unfilled)
                holder.doctorStar.contentDescription = "Lekár nie je obľúbený"
                DoctorsService().removeFromFavourites(doctor.id, activity, activity)
            }
            else {
                holder.doctorStar.setImageResource(R.drawable.star_filled)
                holder.doctorStar.contentDescription = "Lekár je obľúbený"
                DoctorsService().addToFavourites(doctor.id, activity, activity)
            }
            doctor.is_favourite = !doctor.is_favourite
        }
    }

    override fun getItemCount() = doctorsList.size

    class DoctorsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorNameTextView: TextView = view.findViewById(R.id.doctorNameTextView)
        val doctorSpecialisationTextView: TextView = view.findViewById(R.id.doctorSpecialisationTextView)
        val doctorStar: ImageView = view.findViewById(R.id.doctorStar)
    }
}