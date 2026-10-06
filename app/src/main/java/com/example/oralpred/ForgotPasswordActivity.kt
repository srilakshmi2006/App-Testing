package com.example.oralpred

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val backBtn = findViewById<ImageView>(R.id.backBtnForgot)
        val emailInput = findViewById<EditText>(R.id.forgotEmailInput)
        val sendResetBtn = findViewById<Button>(R.id.sendResetBtn)
        val signInLink = findViewById<TextView>(R.id.signInLinkForgot)

        backBtn.setOnClickListener { finish() }
        signInLink.setOnClickListener { finish() }

        sendResetBtn.setOnClickListener {
            val email = emailInput.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Go directly to reset screen for this flow demonstration
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
