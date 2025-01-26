package com.h1194.cuantracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.AppDatabase
import com.h1194.cuantracker.data.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Upload : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDate = findViewById<EditText>(R.id.etDate)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        // Setup Spinner
        val types = arrayOf("Pendapatan", "Pengeluaran")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        spinnerType.adapter = adapter

        // Date Picker Dialog
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        etDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                etDate.setText(dateFormat.format(calendar.time))
            }, year, month, day).show()
        }

        val db = AppDatabase.getDatabase(this)

        btnSubmit.setOnClickListener {
            val type = spinnerType.selectedItem.toString()
            val amount = etAmount.text.toString().toFloatOrNull()
            val selectedDate = etDate.text.toString()

            if (amount != null && selectedDate.isNotEmpty()) {
                val transactionDate = dateFormat.parse(selectedDate) ?: Date()

                val transaction = Transaction(
                    type = type,
                    amount = amount,
                    date = transactionDate
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    db.transactionDao().insertTransaction(transaction)
                    runOnUiThread {
                        Toast.makeText(this@Upload, "Transaction saved", Toast.LENGTH_SHORT).show()
                        etAmount.text.clear()
                        etDate.text.clear()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a valid amount and date", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
