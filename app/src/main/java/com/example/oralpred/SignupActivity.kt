package com.example.oralpred

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val nameInput = findViewById<EditText>(R.id.signupNameInput)
        val emailInput = findViewById<EditText>(R.id.signupEmailInput)
        val passwordInput = findViewById<EditText>(R.id.signupPasswordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.signupConfirmPasswordInput)
        val termsCheck = findViewById<CheckBox>(R.id.termsCheck)
        val createAccountBtn = findViewById<Button>(R.id.createAccountBtn)
        val signInLink = findViewById<TextView>(R.id.signInLink)

        backBtn.setOnClickListener {
            finish() // Go back to login
        }

        signInLink.setOnClickListener {
            finish() // Go back to login
        }

        createAccountBtn.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirm = confirmPasswordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!termsCheck.isChecked) {
                Toast.makeText(this, "You must agree to the terms", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signUpWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    Toast.makeText(this@SignupActivity, "Account created successfully!", Toast.LENGTH_LONG).show()
                    finish() // Return to login page
                } catch (e: Exception) {
                    Toast.makeText(this@SignupActivity, e.localizedMessage ?: "Signup failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
