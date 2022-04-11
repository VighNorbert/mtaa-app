package sk.evysetrenie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sk.evysetrenie.api.model.contracts.responses.DoctorsResponse

class DoctorsAdapter(val doctorsList: List<DoctorsResponse>) : RecyclerView.Adapter<DoctorsAdapter.DoctorsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsHolder {
        return DoctorsHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_doctors, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DoctorsHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class DoctorsHolder(val view: View) : RecyclerView.ViewHolder(view)
}