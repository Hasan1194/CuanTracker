package com.h1194.cuantracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.remote.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val repository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Cek apakah user sudah login
        if (repository.isUserLoggedIn()) {
            navigateToMain()
            return
        }

        val emailEditText = findViewById<EditText>(R.id.emailEditTextLayout)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditTextLayout)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val progressBar = findViewById<LinearProgressIndicator>(R.id.linearProgressBar)
        val registerText = findViewById<TextView>(R.id.createAccountText)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = repository.loginUserWithEmailPassword(email, password)
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        loginButton.isEnabled = true
                        Toast.makeText(this@LoginActivity, "Login berhasil! Selamat datang ${user.name} (${user.role})", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        loginButton.isEnabled = true

                        val errorMessage = when {
                            e.message?.contains("password") == true -> "Password salah"
                            e.message?.contains("user") == true -> "Email tidak terdaftar"
                            e.message?.contains("network") == true -> "Periksa koneksi internet"
                            else -> "Login gagal: ${e.message}"
                        }

                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (repository.isUserLoggedIn()) {
            navigateToMain()
        }
    }
}