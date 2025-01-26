package com.h1194.cuantracker.data.local

import java.util.Date

data class Transaction(
    val id: String = "",
    val type: String = "",
    val amount: Float = 0.0f,
    val date: Date = Date()
)
