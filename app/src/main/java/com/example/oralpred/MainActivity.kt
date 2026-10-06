package com.example.oralpred

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import io.github.jan.supabase.gotrue.auth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation Setup
        val navDashboard = findViewById<android.widget.LinearLayout>(R.id.navDashboard)
        val navAnalyze = findViewById<android.widget.LinearLayout>(R.id.navAnalyze)
        val navReports = findViewById<android.widget.LinearLayout>(R.id.navReports)
        val navSettings = findViewById<android.widget.LinearLayout>(R.id.navSettings)

        navDashboard?.setOnClickListener {
            // Already here
        }

        navAnalyze?.setOnClickListener {
            val intent = android.content.Intent(this, AnalyzeActivity::class.java)
            startActivity(intent)
        }

        navReports?.setOnClickListener {
            val intent = android.content.Intent(this, ReportsActivity::class.java)
            startActivity(intent)
        }

        navSettings?.setOnClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateDashboardStats()
    }

    private fun updateDashboardStats() {
        val aiModel = AIModel(this)
        val reports = aiModel.getSavedReports()

        var high = 0
        var medium = 0
        var low = 0

        for (report in reports) {
            if (report.riskPercentage > 60) high++
            else if (report.riskPercentage > 30) medium++
            else low++
        }

        findViewById<android.widget.TextView>(R.id.statTotal)?.text = reports.size.toString()
        findViewById<android.widget.TextView>(R.id.statHigh)?.text = high.toString()
        findViewById<android.widget.TextView>(R.id.statMedium)?.text = medium.toString()
        findViewById<android.widget.TextView>(R.id.statLow)?.text = low.toString()

        // Fetch User Name from Supabase (or fallback)
        try {
            val session = SupabaseClient.client.auth.currentSessionOrNull()
            
            val dashboardUserName = findViewById<android.widget.TextView>(R.id.dashboardUserName)
            
            if (session != null && session.user != null) {
                // Try to get name from metadata, fallback to email prefix
                val metadata = session.user!!.userMetadata
                var nameStr = "Doctor"
                if (metadata != null && metadata.containsKey("full_name")) {
                    nameStr = metadata["full_name"]?.toString()?.replace("\"", "") ?: "Doctor"
                } else if (session.user!!.email != null) {
                    nameStr = session.user!!.email!!.substringBefore("@").replaceFirstChar { it.uppercase() }
                }
                
                dashboardUserName?.text = nameStr
            }
        } catch (e: Exception) {
            e.printStackTrace()
            findViewById<android.widget.TextView>(R.id.dashboardUserName)?.text = "Doctor"
        }
    }
}
