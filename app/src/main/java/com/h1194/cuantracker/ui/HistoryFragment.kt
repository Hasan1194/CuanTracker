package com.h1194.cuantracker.ui;

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
import com.google.firebase.firestore.FirebaseFirestore
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.local.Transaction
import com.h1194.cuantracker.databinding.FragmentHistoryBinding
import com.h1194.cuantracker.ui.adapter.HistoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.rvStory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()
        loadTransactionsFromFirebase()

        return view
    }

    private fun loadTransactionsFromFirebase() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val transactions = mutableListOf<Transaction>()
                val snapshot = db.collection("transactions").get().await()

                for (document in snapshot.documents) {
                    val transaction = document.toObject(Transaction::class.java)
                    transaction?.let {
                        transactions.add(it)
                    }
                }

                withContext(Dispatchers.Main) {
                    historyAdapter = HistoryAdapter(transactions) { transaction ->
                        deleteTransaction(transaction)
                    }
                    recyclerView.adapter = historyAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                db.collection("transactions").document(transaction.id).delete().await()

                loadTransactionsFromFirebase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}