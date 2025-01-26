package com.h1194.cuantracker.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.h1194.cuantracker.data.local.Transaction
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val transactionCollection = db.collection("transactions")

    suspend fun addTransaction(transaction: Transaction) {
        val data = hashMapOf(
            "type" to transaction.type,
            "amount" to transaction.amount,
            "date" to transaction.date.time
        )
        transactionCollection.add(data).await()
    }

    suspend fun getAllTransactions(): List<Transaction> {
        val snapshot = transactionCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val type = doc.getString("type")
            val amount = doc.getDouble("amount")?.toFloat()
            val date = doc.getLong("date")?.let { Date(it) }

            if (type != null && amount != null && date != null) {
                Transaction(type = type, amount = amount, date = date)
            } else null
        }
    }

    suspend fun deleteTransaction(transactionId: String) {
        transactionCollection.document(transactionId).delete().await()
    }
}