package sk.evysetrenie

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.api.model.WorkSchedule


class WorkSchedulesAdapter(private val workScheduleList: MutableList<WorkSchedule>, private val activity: RegisterDoctorActivity) : RecyclerView.Adapter<WorkSchedulesAdapter.WorkSchedulesHolder>() {

    private val days : Array<String> = arrayOf("Pondelok", "Utorok", "Streda", "Štvrtok", "Piatok", "Sobota", "Nedeľa")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkSchedulesHolder {
        return WorkSchedulesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_work_schedules, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkSchedulesHolder, position: Int) {
        val adapter = ArrayAdapter(activity, R.layout.list_item, days)
        (holder.dayTextView as? AutoCompleteTextView)?.setAdapter(adapter)

        holder.timeFromTextView.isClickable = true
        holder.timeFromTextView.isLongClickable = false
        holder.timeFromTextView.inputType = InputType.TYPE_NULL
        holder.timeFromTextView.setOnClickListener { showTimePicker() }

        holder.timeToTextView.isClickable = true
        holder.timeToTextView.isLongClickable = false
        holder.timeToTextView.inputType = InputType.TYPE_NULL
        holder.timeToTextView.setOnClickListener { showTimePicker() }

        holder.removeButton.setOnClickListener{
            workScheduleList.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
//            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun getItemCount() = workScheduleList.size

    private fun showTimePicker() {
        val mcurrentTime: Calendar = Calendar.getInstance()
        val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute: Int = mcurrentTime.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(activity, { _, _, _ -> }, hour, minute, true)
        timePickerDialog.show()
    }

    fun addItem(ws: WorkSchedule) {
        workScheduleList.add(ws)
        notifyDataSetChanged()
//        notifyItemInserted(itemCount - 1)
        println(workScheduleList)
    }

    class WorkSchedulesHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        val timeFromTextView: TextView = view.findViewById(R.id.timeFromTextView)
        val timeToTextView: TextView = view.findViewById(R.id.timeToTextView)
        val removeButton: ImageView = view.findViewById(R.id.removeButton)
    }
}