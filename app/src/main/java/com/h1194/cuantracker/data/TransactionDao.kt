package com.h1194.cuantracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transaction_table")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("SELECT SUM(amount) FROM transaction_table WHERE type = :type")
    suspend fun getTotalByType(type: String): Float

    @Query("SELECT SUM(amount) FROM transaction_table WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalByTypeAndDateRange(type: String, startDate: Date, endDate: Date): Float
}