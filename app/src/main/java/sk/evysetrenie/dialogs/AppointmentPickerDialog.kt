package sk.evysetrenie.dialogs

import android.app.AlertDialog
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import sk.evysetrenie.R
import sk.evysetrenie.BaseActivity
import sk.evysetrenie.DoctorsDetailActivity
import sk.evysetrenie.adapters.AppointmentsAdapter
import sk.evysetrenie.api.model.contracts.responses.AppointmentTimesResponse
import java.util.*

class AppointmentPickerDialog(
    private val activity: DoctorsDetailActivity,
) {
    private lateinit var dialog: AlertDialog

    fun open(times: List<AppointmentTimesResponse>) {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val v = inflater.inflate(R.layout.appointment_picker_dialog, null)
        builder.setView(v)
        builder.setCancelable(true)

        val appointments = mutableListOf<String>()
        val appointmentsMap = mutableMapOf<String, Int>()
        var timeslot: String
        for (time in times) {
            timeslot = time.time_from.dropLast(3) + " - " + time.time_to.dropLast(3)
            appointments.add(timeslot)
            appointmentsMap[timeslot] = time.id
        }

        val timePicker = v.findViewById<NumberPicker>(R.id.timePicker)
        timePicker.minValue = 0
        timePicker.maxValue = appointments.size - 1
        timePicker.displayedValues = appointments.toTypedArray()
        timePicker.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        timePicker.wrapSelectorWheel = false

        v.findViewById<Button>(R.id.timeSetConfirm).setOnClickListener {
            dialog.dismiss()
            activity.timePicked(appointmentsMap[appointments[timePicker.value]], appointments[timePicker.value])
        }

        v.findViewById<Button>(R.id.timeSetCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog = builder.create()
        dialog.show()
    }

}