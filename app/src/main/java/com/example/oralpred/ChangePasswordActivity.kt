package com.example.oralpred

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSavePassword = findViewById<Button>(R.id.btnSavePassword)

        backBtn.setOnClickListener { finish() }

        btnSavePassword.setOnClickListener {
            val p1 = etNewPassword.text.toString()
            val p2 = etConfirmPassword.text.toString()

            if (p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "Please enter new password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (p1 != p2) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSavePassword.text = "Saving..."
            btnSavePassword.isEnabled = false

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.updateUser {
                        this.password = p1
                    }
                    Toast.makeText(this@ChangePasswordActivity, "Password updated successfully!", Toast.LENGTH_LONG).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@ChangePasswordActivity, e.localizedMessage ?: "Update failed", Toast.LENGTH_LONG).show()
                    btnSavePassword.text = "Save Password"
                    btnSavePassword.isEnabled = true
                }
            }
        }
    }
}
