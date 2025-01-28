package com.h1194.cuantracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.local.Transaction
import com.h1194.cuantracker.data.remote.FirestoreRepository
import com.h1194.cuantracker.ui.adapter.HistoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private val repository = FirestoreRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.rvStory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()

        historyAdapter = HistoryAdapter(emptyList()) { transaction ->
            deleteTransaction(transaction)
        }
        recyclerView.adapter = historyAdapter

        loadTransactionsFromRepository()

        return view
    }

    private fun loadTransactionsFromRepository() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val transactions = repository.getAllTransactions()

                withContext(Dispatchers.Main) {
                    historyAdapter.updateData(transactions)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load transactions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                repository.deleteTransaction(transaction.id)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Transaction deleted", Toast.LENGTH_SHORT).show()

                    loadTransactionsFromRepository()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to delete transaction", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
