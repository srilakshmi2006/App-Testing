package com.example.oralpred

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val backBtn = findViewById<ImageView>(R.id.backBtnReset)
        val newPasswordInput = findViewById<EditText>(R.id.resetPasswordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.resetConfirmPasswordInput)
        val saveBtn = findViewById<Button>(R.id.savePasswordBtn)

        backBtn.setOnClickListener { finish() }

        saveBtn.setOnClickListener {
            val password = newPasswordInput.text.toString()
            val confirm = confirmPasswordInput.text.toString()

            if (password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Password saved successfully!", Toast.LENGTH_SHORT).show()
            finish() // Returns to login screen in this flow
        }
    }
}
