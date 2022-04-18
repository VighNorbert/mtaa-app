package sk.evysetrenie.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.AppointmentsActivity
import sk.evysetrenie.R
import sk.evysetrenie.api.AuthState
import sk.evysetrenie.api.model.contracts.responses.AppointmentResponse
import sk.evysetrenie.dialogs.ConfirmDialog
import java.text.SimpleDateFormat
import java.util.*

class AppointmentsAdapter(private val appointmentsList: MutableList<AppointmentResponse>, val activity: AppointmentsActivity) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentsHolder>() {

    private var toBeRemoved: Int = 0

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
        holder.confirmDialog = ConfirmDialog(activity, this, holder.adapterPosition)

        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val currentTime = SimpleDateFormat("HH:mm").format(Date())
        if (appointment.date < currentDate || (appointment.date == currentDate && appointment.time_to < currentTime)) {
            holder.appointmentRemoveButton.visibility = View.GONE
        } else if (appointment.date == currentDate && appointment.time_to > currentTime && appointment.time_from <= currentTime) {
            if (appointment.type == "O") {
                holder.appointmentCallButton.visibility = View.VISIBLE
                holder.appointmentCallButton.setOnClickListener {
                    activity.startMeeting(appointment.id)
                }
            }
            holder.appointmentLayout.setBackgroundResource(R.color.primaryLightColor)
            holder.appointmentRemoveButton.visibility = View.GONE
        }
        holder.appointmentRemoveButton.setOnClickListener {
            holder.confirmDialog.open()
        }
    }


    fun remove(position: Int) {
        toBeRemoved = position
        activity.removeAppointment(appointmentsList[position])
    }

    fun removeSuccess() {
        appointmentsList.removeAt(toBeRemoved)
        notifyItemRemoved(toBeRemoved)
        notifyItemRangeChanged(toBeRemoved, itemCount - toBeRemoved)
    }

    override fun getItemCount() = appointmentsList.size

    class AppointmentsHolder(view: View) : RecyclerView.ViewHolder(view) {
        var appointmentLayout : ConstraintLayout = view.findViewById(R.id.layout)
        val appointmentRemoveButton: ImageView = view.findViewById(R.id.removeButton)
        val appointmentCallButton: ImageView = view.findViewById(R.id.callButton)
        val appointmentTimeTextView: TextView = view.findViewById(R.id.appointmentTimeTextView)
        val appointmentNameTextView: TextView = view.findViewById(R.id.appointmentNameTextView)
        val appointmentDescriptionTextView: TextView = view.findViewById(R.id.appointmentDescriptionTextView)
        lateinit var confirmDialog: ConfirmDialog
    }
}