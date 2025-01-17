package com.h1194.cuantracker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.lifecycleScope;
import com.h1194.cuantracker.R;
import com.h1194.cuantracker.data.AppDatabase;
import com.h1194.cuantracker.data.Transaction;
import java.util.Date;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch

class Upload : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
                val etAmount = findViewById<EditText>(R.id.etAmount)
                val btnSubmit = findViewById<Button>(R.id.btnSubmit)

                val types = arrayOf("Pendapatan", "Pengeluaran")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        spinnerType.adapter = adapter

        val db = AppDatabase.getDatabase(this)

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

                lifecycleScope.launch(Dispatchers.IO) {
                    db.transactionDao().insertTransaction(transaction)
                    runOnUiThread {
                        Toast.makeText(this@Upload, "Transaction saved", Toast.LENGTH_SHORT).show()
                        etAmount.text.clear()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}