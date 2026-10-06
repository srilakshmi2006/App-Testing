package com.example.oralpred

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)
        val navAnalyze = findViewById<LinearLayout>(R.id.navAnalyze)
        val navReports = findViewById<LinearLayout>(R.id.navReports)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)
        val logoutBtn = findViewById<LinearLayout>(R.id.logoutBtn)
        
        val profileName = findViewById<TextView>(R.id.profileName)
        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val btnEditProfile = findViewById<LinearLayout>(R.id.btnEditProfile)
        val btnChangePassword = findViewById<LinearLayout>(R.id.btnChangePassword)
        val btnHelp = findViewById<LinearLayout>(R.id.btnHelp)
        val btnPrivacy = findViewById<LinearLayout>(R.id.btnPrivacy)
        val btnTerms = findViewById<LinearLayout>(R.id.btnTerms)
        val btnAbout = findViewById<LinearLayout>(R.id.btnAbout)

        lifecycleScope.launch {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            if (user != null) {
                profileEmail.text = user.email
                val name = user.userMetadata?.get("full_name")?.toString()?.replace("\"", "") ?: "User"
                profileName.text = name
            }
        }

        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        btnHelp.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtra("TITLE", "Help & Support")
            intent.putExtra("POINTS", arrayOf(
                "1. Welcome to the OralPred support center.",
                "2. For general inquiries, check out our website FAQ.",
                "3. To report a bug, please use the Help button below.",
                "4. Standard response time is 24-48 business hours.",
                "5. Keep your app updated for the best experience."
            ))
            intent.putExtra("SHOW_HELP", true)
            startActivity(intent)
        }

        btnPrivacy.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtra("TITLE", "Privacy Policy")
            intent.putExtra("POINTS", arrayOf(
                "1. We securely encrypt all medical data in transit and at rest.",
                "2. Patient data is never sold to third-party data brokers.",
                "3. We comply with standard health data security practices.",
                "4. Diagnostic data is only accessible to authorized users.",
                "5. You can delete your account and data permanently at any time."
            ))
            intent.putExtra("SHOW_HELP", false)
            startActivity(intent)
        }

        btnTerms.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtra("TITLE", "Terms & Conditions")
            intent.putExtra("POINTS", arrayOf(
                "1. This application is designed to assist, not replace, medical professionals.",
                "2. AI predictions are supplementary and should be verified.",
                "3. Users are responsible for maintaining account security.",
                "4. Unauthorized use or data scraping is strictly prohibited.",
                "5. We reserve the right to suspend abusive accounts."
            ))
            intent.putExtra("SHOW_HELP", false)
            startActivity(intent)
        }

        btnAbout.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtra("TITLE", "About App")
            intent.putExtra("POINTS", arrayOf(
                "1. OralPred Version 1.0.0",
                "2. Developed for modern medical diagnostics.",
                "3. Uses advanced machine learning to predict oral cancer risk.",
                "4. Built with web technologies and robust secure backend.",
                "5. Thank you for using OralPred!"
            ))
            intent.putExtra("SHOW_HELP", false)
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            finish()
        }

        navDashboard.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        navAnalyze.setOnClickListener {
            val intent = Intent(this, AnalyzeActivity::class.java)
            startActivity(intent)
            finish()
        }

        navReports.setOnClickListener {
            val intent = Intent(this, ReportsActivity::class.java)
            startActivity(intent)
            finish()
        }

        navSettings.setOnClickListener {
            // Already here
        }

        logoutBtn.setOnClickListener {
            lifecycleScope.launch {
                SupabaseClient.client.auth.signOut()
                val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}
