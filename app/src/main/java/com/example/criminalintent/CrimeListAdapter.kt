package com.example.criminalintent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.ListItemCrimeBinding
import com.example.criminalintent.model.Crime
import com.google.android.material.timepicker.TimeFormat
import java.text.DateFormat
import java.util.Date
import java.util.UUID

private const val CRIME_LIST_ADAPTER_TAG = "CRIME_LIST_ADAPTER_TAG"

//создаёт и редачит viewHolder
class CrimeListAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClick: (crimeId: UUID) -> Unit
) : RecyclerView.Adapter<CrimeHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (crimes[position].isSolved) 1 else 0
    }

    //3 function
    //inflate макет, пихаем его в контейнер
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        //Log.d(CRIME_LIST_ADAPTER_TAG, "view type: $viewType")
        //inflate and binding
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)

        //create crimeHolder
        //returning
        return CrimeHolder(binding)
    }

    //связываем данный с представлением
    //we have date of necessary crime and its binding
    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        holder.bind(crimes[position], onCrimeClick)
    }

    override fun getItemCount() = crimes.size

}

//to store view of item list
class CrimeHolder(
    private val binding: ListItemCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime, onCrimeClick: (crimeId: UUID) -> Unit) {
        binding.apply {
            crimeTitle.text = crime.title

            crimeDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(crime.date) + " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(crime.date)

            root.setOnClickListener {
                onCrimeClick(crime.id)
            }

            //symbol _ was changed to camelCase
            crimeSolved.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }
    }
}
