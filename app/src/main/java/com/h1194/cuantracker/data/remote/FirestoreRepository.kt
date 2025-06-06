package com.h1194.cuantracker.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h1194.cuantracker.data.local.Transaction
import com.h1194.cuantracker.data.local.User
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private val transactionCollection = db.collection("transactions")
    private val userCollection = db.collection("user")

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
        role: String
    ): String {

        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Gagal mendapatkan UID")

        val user = hashMapOf(
            "id" to uid,
            "name" to name,
            "email" to email,
            "role" to role
        )

        userCollection.document(uid).set(user).await()

        return uid
    }

    suspend fun loginUserWithEmailPassword(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Gagal mendapatkan UID")

        return getUserById(uid)
    }

    suspend fun getUserById(uid: String): User {
        val document = userCollection.document(uid).get().await()

        if (!document.exists()) {
            throw Exception("Data user tidak ditemukan")
        }

        return User(
            id = document.getString("id") ?: "",
            name = document.getString("name") ?: "",
            email = document.getString("email") ?: "",
            role = document.getString("role") ?: ""
        )
    }

    suspend fun getCurrentUser(): User? {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            try {
                getUserById(currentUser.uid)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
    }

    fun getAvailableRoles(): List<String> {
        return listOf("owner", "admin", "karyawan")
    }

}