package com.h1194.cuantracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.lifecycleScope;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.h1194.cuantracker.R;
import com.h1194.cuantracker.data.AppDatabase;
import com.h1194.cuantracker.data.Transaction;
import com.h1194.cuantracker.ui.adapter.HistoryAdapter
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext;

class HistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: AppDatabase

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
            val view = inflater.inflate(R.layout.fragment_history, container, false)

            recyclerView = view.findViewById(R.id.rvStory)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            db = AppDatabase.getDatabase(requireContext())

            lifecycleScope.launch(Dispatchers.IO) {
            val transactions = db.transactionDao().getAllTransactions()

            withContext(Dispatchers.Main) {
            historyAdapter = HistoryAdapter(transactions) { transaction ->
            deleteTransaction(transaction)
    }
            recyclerView.adapter = historyAdapter
    }
    }

    return view
    }

    private fun deleteTransaction(transaction: Transaction) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.transactionDao().deleteTransaction(transaction)

            val updatedTransactions = db.transactionDao().getAllTransactions()
            withContext(Dispatchers.Main) {
                historyAdapter = HistoryAdapter(updatedTransactions) { transaction ->
                        deleteTransaction(transaction)
                }
                recyclerView.adapter = historyAdapter
            }
        }
    }
}