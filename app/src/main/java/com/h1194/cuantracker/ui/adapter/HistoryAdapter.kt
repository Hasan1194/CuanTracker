package com.h1194.cuantracker.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.Transaction;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

class HistoryAdapter(
    private val data: List<Transaction>,
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val typeTextView: TextView = view.findViewById(R.id.tvType)
    val amountTextView: TextView = view.findViewById(R.id.tvAmount)
    val dateTextView: TextView = view.findViewById(R.id.tvDate)
    val deleteButton: FloatingActionButton = view.findViewById(R.id.btn_delete)
}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
    return ViewHolder(view)
}

override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val transaction = data[position]
    holder.typeTextView.text = transaction.type

    val formattedAmount = currencyFormatter.format(transaction.amount)
            .replace("IDR", "Rp")
    holder.amountTextView.text = formattedAmount

    holder.dateTextView.text = dateFormatter.format(transaction.date)

    holder.deleteButton.setOnClickListener {
        onDeleteClick(transaction)
    }
}

override fun getItemCount() = data.size
}