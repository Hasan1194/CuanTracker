package com.h1194.cuantracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.h1194.cuantracker.R
import com.h1194.cuantracker.data.remote.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val repository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameInput = findViewById<EditText>(R.id.nameEditTextLayout)
        val emailInput = findViewById<EditText>(R.id.emailEditTextLayout)
        val passwordInput = findViewById<EditText>(R.id.passwordEditTextLayout)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val registerButton = findViewById<Button>(R.id.loginButton)
        val progressBar = findViewById<LinearProgressIndicator>(R.id.linearProgressBar)
        val loginText = findViewById<TextView>(R.id.createAccountText)

        // Setup spinner untuk role
        setupRoleSpinner(roleSpinner)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val selectedRole = roleSpinner.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading
            progressBar.visibility = View.VISIBLE
            registerButton.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    repository.registerUserWithEmailPassword(name, email, password, selectedRole)
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        registerButton.isEnabled = true
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil sebagai $selectedRole", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        registerButton.isEnabled = true
                        Toast.makeText(this@RegisterActivity, "Registrasi gagal: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            loginText.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupRoleSpinner(spinner: Spinner) {
        val roles = repository.getAvailableRoles()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val defaultPosition = roles.indexOf("karyawan")
        if (defaultPosition >= 0) {
            spinner.setSelection(defaultPosition)
        }
    }
}