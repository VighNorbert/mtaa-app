package sk.evysetrenie

import android.app.TimePickerDialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.api.model.WorkSchedule


class WorkSchedulesAdapter(private val workScheduleList: MutableList<WorkSchedule>, private val activity: BaseActivity) : RecyclerView.Adapter<WorkSchedulesAdapter.WorkSchedulesHolder>() {

    private val days : Array<String> = arrayOf("Nedeľa", "Pondelok", "Utorok", "Streda", "Štvrtok", "Piatok", "Sobota")

    var changed : Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkSchedulesHolder {
        return WorkSchedulesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_work_schedules, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WorkSchedulesHolder, position: Int) {
        val workSchedule = workScheduleList[position]

        val adapter = ArrayAdapter(activity, R.layout.list_item, days)
        (holder.dayTextView as? AutoCompleteTextView)?.setAdapter(adapter)
        (holder.dayTextView as? AutoCompleteTextView)?.setText(days[workSchedule.weekday], false)
        (holder.dayTextView as? AutoCompleteTextView)?.setOnItemClickListener { _, _, i, _ ->
            holder.dayTextView.setText(days[i], false)
            changed = true
            workSchedule.weekday = i
        }

        holder.timeFromTextView.isClickable = true
        holder.timeFromTextView.isLongClickable = false
        holder.timeFromTextView.inputType = InputType.TYPE_NULL
        holder.timeFromTextView.setOnFocusChangeListener { _, b -> if (b) showTimePicker(holder, workSchedule, true) }
        holder.timeFromTextView.setOnClickListener { showTimePicker(holder, workSchedule, true) }
        holder.timeFromTextView.text = formatTime(workSchedule.time_from)

        holder.timeToTextView.isClickable = true
        holder.timeToTextView.isLongClickable = false
        holder.timeToTextView.inputType = InputType.TYPE_NULL
        holder.timeToTextView.setOnFocusChangeListener { _, b -> if (b) showTimePicker(holder, workSchedule, false) }
        holder.timeToTextView.setOnClickListener { showTimePicker(holder, workSchedule, false) }
        holder.timeToTextView.text = formatTime(workSchedule.time_to)

        holder.removeButton.setOnClickListener{
            val pos = holder.adapterPosition
            workScheduleList.removeAt(pos)
            changed = true
            notifyItemRemoved(pos)
        }
    }

    private fun formatTime(time: String) : String {
        val split = time.split(':')
        return String.format("%02d:%02d", split[0].toInt(), split[1].toInt())
    }

    override fun getItemCount() = workScheduleList.size

    private fun showTimePicker(holder: WorkSchedulesHolder, workSchedule: WorkSchedule, isFrom: Boolean) {
        val oldTimeSplit = (if (isFrom) workSchedule.time_from else workSchedule.time_to).split(':')
        val hour: Int = oldTimeSplit[0].toInt()
        val minute: Int = oldTimeSplit[1].toInt()
        val timePickerDialog = TimePickerDialog(activity, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            if (isFrom) {
                holder.timeFromTextView.text = time
                workSchedule.time_from = "$time:00"
            } else {
                holder.timeToTextView.text = time
                workSchedule.time_to = "$time:00"
            }
            changed = true
        }, hour, minute, true)
        timePickerDialog.show()
    }

    fun addItem(ws: WorkSchedule) {
        workScheduleList.add(ws)
        notifyItemInserted(itemCount - 1)
        changed = true
        println(workScheduleList)
    }

    class WorkSchedulesHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        val timeFromTextView: TextView = view.findViewById(R.id.timeFromTextView)
        val timeToTextView: TextView = view.findViewById(R.id.timeToTextView)
        val removeButton: ImageView = view.findViewById(R.id.removeButton)
    }
}