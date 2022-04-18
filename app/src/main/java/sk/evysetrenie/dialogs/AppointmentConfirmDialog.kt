package sk.evysetrenie.dialogs

import android.app.AlertDialog
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import sk.evysetrenie.AppointmentsActivity
import sk.evysetrenie.R
import sk.evysetrenie.BaseActivity
import sk.evysetrenie.DoctorsDetailActivity
import sk.evysetrenie.adapters.AppointmentsAdapter

class AppointmentConfirmDialog(
    private val activity: DoctorsDetailActivity,
) {
    private lateinit var dialog: AlertDialog

    fun open() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val v = inflater.inflate(R.layout.appointment_confirm_dialog, null)
        builder.setView(v)
        builder.setCancelable(true)

        v.findViewById<TextView>(R.id.appconfirm_date).text = activity.detailAppointmentDate.text
        v.findViewById<TextView>(R.id.appconfirm_time).text = activity.detailAppointmentTime.text
        v.findViewById<TextView>(R.id.appconfirm_type).text = "Typ: " + activity.detailAppointmentTypeText.text
        v.findViewById<TextView>(R.id.appconfirm_description).text = "Popis: " + activity.detailAppointmentDescriptionText.text

        v.findViewById<Button>(R.id.appconfirm_buttonConfirm).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(activity, AppointmentsActivity::class.java)
            activity.startActivity(intent)
        }
        dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

}