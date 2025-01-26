package com.h1194.cuantracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.local.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeAdapter(private var data: List<Transaction>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeTextView: TextView = view.findViewById(R.id.tvType)
        val amountTextView: TextView = view.findViewById(R.id.tvAmount)
        val dateTextView: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardhome, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = data[position]
        holder.typeTextView.text = transaction.type

        val formattedAmount = currencyFormatter.format(transaction.amount ?: 0)
            .replace("IDR", "Rp")
        holder.amountTextView.text = formattedAmount

        holder.dateTextView.text = dateFormatter.format(transaction.date ?: Date())
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<Transaction>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = data.size
            override fun getNewListSize() = newData.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition].id == newData[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition] == newData[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = newData
        diffResult.dispatchUpdatesTo(this)
    }
}
