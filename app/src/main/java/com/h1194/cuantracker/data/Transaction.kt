package com.h1194.cuantracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "amount")
    val amount: Float,
    @ColumnInfo(name = "date")
    val date: Date,
)