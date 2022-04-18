package sk.evysetrenie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.AppointmentsActivity
import sk.evysetrenie.R
import sk.evysetrenie.api.model.AppointmentDate

class DatesAdapter(private val datesList: MutableList<AppointmentDate>, val activity: AppointmentsActivity) : RecyclerView.Adapter<DatesAdapter.AppointmentDatesHolder>() {

    private val weekdays = arrayOf("Po", "Ut", "St", "Št", "Pi", "So", "Ne")

    private var activePosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentDatesHolder {
        return AppointmentDatesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_date, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AppointmentDatesHolder, position: Int) {
        val appointmentDate = datesList[position]
        holder.date.text = appointmentDate.date.dayOfMonth.toString()
        holder.weekday.text = weekdays[appointmentDate.date.dayOfWeek.value - 1]
        if (holder.adapterPosition == activePosition) {
            holder.layout.setBackgroundResource(R.drawable.date_selected)
            holder.date.setTextColor(ContextCompat.getColor(activity.applicationContext, R.color.white))
            holder.weekday.setTextColor(ContextCompat.getColor(activity.applicationContext, R.color.white))
        } else if (appointmentDate.appointments.size > 0) {
            holder.layout.setBackgroundResource(R.drawable.date_scheduled)
            holder.date.setTextColor(ContextCompat.getColor(activity.applicationContext, R.color.black))
            holder.weekday.setTextColor(ContextCompat.getColor(activity.applicationContext, R.color.black))
        } else {
            holder.layout.setBackgroundResource(R.drawable.date_empty)
            holder.date.setTextColor(ContextCompat.getColor(activity.applicationContext, R.color.black))
            holder.weekday.setTextColor(ContextCompat.getColor(activity.applicationContext, R.color.black))
        }
        holder.layout.setOnClickListener { itemSelected(holder.adapterPosition) }
    }

    fun itemSelected(position: Int) {
        val oldPos = activePosition
        activePosition = position
        notifyItemChanged(oldPos)
        notifyItemChanged(activePosition)
        activity.changeAppointments(datesList[position].appointments)
    }

    override fun getItemCount() = datesList.size

    class AppointmentDatesHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.layout)
        val weekday: TextView = view.findViewById(R.id.weekday)
        val date: TextView = view.findViewById(R.id.date)
    }
}