package com.h1194.cuantracker.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.remote.FirestoreRepository
import com.h1194.cuantracker.data.local.Transaction
import kotlinx.coroutines.launch
import java.util.Date

class Upload : AppCompatActivity() {

    private val repository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        val types = arrayOf("Pendapatan", "Pengeluaran")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        spinnerType.adapter = adapter

        btnSubmit.setOnClickListener {
            val type = spinnerType.selectedItem.toString()
            val amount = etAmount.text.toString().toFloatOrNull()
            val currentDate = Date()

            if (amount != null) {
                val transaction = Transaction(
                    type = type,
                    amount = amount,
                    date = currentDate
                )

                lifecycleScope.launch {
                    try {
                        repository.addTransaction(transaction)
                        Toast.makeText(this@Upload, "Transaction saved", Toast.LENGTH_SHORT).show()
                        etAmount.text.clear()
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@Upload, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

