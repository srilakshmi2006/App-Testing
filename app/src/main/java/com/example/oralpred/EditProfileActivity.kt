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

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSaveProfile = findViewById<Button>(R.id.btnSaveProfile)

        backBtn.setOnClickListener { finish() }

        lifecycleScope.launch {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            if (user != null) {
                etEmail.setText(user.email)
                val name = user.userMetadata?.get("full_name")?.toString()?.replace("\"", "") ?: ""
                etName.setText(name)
            }
        }

        btnSaveProfile.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please enter name and email.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSaveProfile.text = "Saving..."
            btnSaveProfile.isEnabled = false

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.updateUser {
                        this.email = email
                        this.data = kotlinx.serialization.json.buildJsonObject {
                            put("full_name", kotlinx.serialization.json.JsonPrimitive(name))
                        }
                    }
                    Toast.makeText(this@EditProfileActivity, "Profile updated!", Toast.LENGTH_LONG).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@EditProfileActivity, e.localizedMessage ?: "Update failed", Toast.LENGTH_LONG).show()
                    btnSaveProfile.text = "Save Profile"
                    btnSaveProfile.isEnabled = true
                }
            }
        }
    }
}
