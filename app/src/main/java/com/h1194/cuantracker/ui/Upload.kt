package com.h1194.cuantracker.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.remote.FirestoreRepository
import com.h1194.cuantracker.data.local.Transaction
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class Upload : AppCompatActivity() {

    private val repository = FirestoreRepository()
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDate = findViewById<EditText>(R.id.etDate) // EditText untuk tanggal
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        val types = arrayOf("Pendapatan", "Pengeluaran")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        spinnerType.adapter = adapter

        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        btnSubmit.setOnClickListener {
            val type = spinnerType.selectedItem.toString()
            val amount = etAmount.text.toString().toFloatOrNull()

            if (amount != null && selectedDate != null) {
                val transaction = Transaction(
                    type = type,
                    amount = amount,
                    date = selectedDate!!
                )

                lifecycleScope.launch {
                    try {
                        repository.addTransaction(transaction)
                        Toast.makeText(this@Upload, "Transaction saved", Toast.LENGTH_SHORT).show()
                        etAmount.text.clear()
                        etDate.text.clear()
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@Upload, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter valid amount and date", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(etDate: EditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.time

                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etDate.setText(formattedDate.format(selectedDate!!))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}


