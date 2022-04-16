package sk.evysetrenie.dialogs

import android.app.AlertDialog
import android.widget.Button
import sk.evysetrenie.R
import sk.evysetrenie.BaseActivity
import sk.evysetrenie.adapters.AppointmentsAdapter

class ConfirmDialog(
    private val activity: BaseActivity,
    private val adapter: AppointmentsAdapter,
    private val adapterPosition: Int
) {
    private lateinit var dialog: AlertDialog

    fun open() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val v = inflater.inflate(R.layout.confirm_dialog, null)
        builder.setView(v)
        builder.setCancelable(true)

        v.findViewById<Button>(R.id.buttonYes).setOnClickListener {
            dialog.dismiss()
            adapter.remove(adapterPosition)
        }
        v.findViewById<Button>(R.id.buttonNo).setOnClickListener {
            dialog.dismiss()
        }

        dialog = builder.create()
        dialog.show()
    }

}