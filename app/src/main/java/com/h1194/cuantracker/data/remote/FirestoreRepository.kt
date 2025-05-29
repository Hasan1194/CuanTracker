package com.h1194.cuantracker.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h1194.cuantracker.data.local.Transaction
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private val transactionCollection = db.collection("transactions")

    suspend fun addTransaction(transaction: Transaction) {
        val data = hashMapOf(
            "type" to transaction.type,
            "amount" to transaction.amount,
            "date" to transaction.date
        )
        transactionCollection.add(data).await()
    }

    suspend fun getAllTransactions(): List<Transaction> {
        val snapshot = transactionCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val type = doc.getString("type")
            val amount = doc.getDouble("amount")?.toFloat()
            val date = doc.getDate("date")

            if (type != null && amount != null && date != null) {
                Transaction(type = type, amount = amount, date = date)
            } else null
        }
    }

    suspend fun deleteTransaction(transactionId: String) {
        transactionCollection.document(transactionId).delete().await()
    }

    suspend fun registerUserWithEmailPassword(
        name: String,
        email: String,
        password: String,
        role: String = "owner"
    ): String {

        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Gagal mendapatkan UID")

        val user = hashMapOf(
            "id" to uid,
            "name" to name,
            "email" to email,
            "role" to role
        )

        db.collection("user").document(uid).set(user).await()

        return uid
    }




}
