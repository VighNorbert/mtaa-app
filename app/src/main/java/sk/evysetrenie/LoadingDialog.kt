package sk.evysetrenie

import android.app.AlertDialog

class LoadingDialog(_activity: BaseActivity) {
    private var activity: BaseActivity = _activity
    private lateinit var dialog: AlertDialog

    fun open() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}