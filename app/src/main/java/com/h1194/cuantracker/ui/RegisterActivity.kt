package com.h1194.cuantracker.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.remote.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val repository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameInput = findViewById<EditText>(R.id.nameEditTextLayout)
        val emailInput = findViewById<EditText>(R.id.emailEditTextLayout)
        val passwordInput = findViewById<EditText>(R.id.passwordEditTextLayout)
        val registerButton = findViewById<Button>(R.id.loginButton)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    repository.registerUserWithEmailPassword(name, email, password)
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registrasi gagal: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
