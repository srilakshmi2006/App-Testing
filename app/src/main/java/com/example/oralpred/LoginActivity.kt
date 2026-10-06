package com.example.oralpred

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signInBtn = findViewById<Button>(R.id.signInBtn)
        val signUpLink = findViewById<TextView>(R.id.signUpLink)
        val forgotPasswordLink = findViewById<TextView>(R.id.forgotPasswordLink)

        signUpLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


        signInBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, e.localizedMessage ?: "Login failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
