package sk.evysetrenie.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.AppointmentsActivity
import sk.evysetrenie.R
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.model.contracts.responses.AppointmentResponse
import java.text.SimpleDateFormat

class AppointmentsAdapter(private val appointmentsList: List<AppointmentResponse>, val activity: AppointmentsActivity) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsHolder {
        return AppointmentsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_appointments, parent, false)
        )
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: AppointmentsHolder, position: Int) {
        val appointment = appointmentsList[position]
        holder.appointmentNameTextView.text =
            if (AuthState.isDoctor()!!)
                appointment.patient.name + " " + appointment.patient.surname
            else
                appointment.doctor.title + " " + appointment.doctor.name + " " + appointment.doctor.surname
        holder.appointmentTimeTextView.text = SimpleDateFormat("dd.MM.yyyy").format(SimpleDateFormat("yyyy-MM-dd").parse(appointment.date)!!) + " " + appointment.time_from + " - " + appointment.time_to
        holder.appointmentDescriptionTextView.text = appointment.description
    }

    override fun getItemCount() = appointmentsList.size

    class AppointmentsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appointmentTimeTextView: TextView = view.findViewById(R.id.appointmentTimeTextView)
        val appointmentNameTextView: TextView = view.findViewById(R.id.appointmentNameTextView)
        val appointmentDescriptionTextView: TextView = view.findViewById(R.id.appointmentDescriptionTextView)
    }
}