package com.example.oralpred

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class ReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)
        val navAnalyze = findViewById<LinearLayout>(R.id.navAnalyze)
        val navReports = findViewById<LinearLayout>(R.id.navReports)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)

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
            // Already here
        }

        navSettings?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadReports()
    }

    private fun loadReports() {
        val container = findViewById<LinearLayout>(R.id.reportsContainer)
        val emptyText = findViewById<android.widget.TextView>(R.id.emptyReportsText)
        
        val aiModel = AIModel(this)
        val reports = aiModel.getSavedReports()

        if (reports.isEmpty()) {
            emptyText.visibility = android.view.View.VISIBLE
            return
        } else {
            emptyText.visibility = android.view.View.GONE
        }

        for (report in reports) {
            val view = layoutInflater.inflate(R.layout.item_report, container, false)
            
            val avatarText = view.findViewById<android.widget.TextView>(R.id.avatarText)
            val patientNameText = view.findViewById<android.widget.TextView>(R.id.patientNameText)
            val dateText = view.findViewById<android.widget.TextView>(R.id.dateText)
            val classificationBadge = view.findViewById<android.widget.TextView>(R.id.classificationBadge)
            val stageText = view.findViewById<android.widget.TextView>(R.id.stageText)
            val riskPercentageText = view.findViewById<android.widget.TextView>(R.id.riskPercentageText)
            
            // Generate initials
            val initials = report.patientName.split(" ").joinToString("") { it.take(1) }.uppercase().take(2)
            avatarText.text = initials
            
            patientNameText.text = report.patientName
            dateText.text = report.date
            
            classificationBadge.text = report.classification
            classificationBadge.setBackgroundColor(android.graphics.Color.parseColor(report.classificationColorHex))
            
            stageText.text = report.stage
            
            riskPercentageText.text = "${report.riskPercentage}%"
            riskPercentageText.setTextColor(android.graphics.Color.parseColor(report.classificationColorHex))

            view.setOnClickListener {
                val intent = Intent(this, AnalysisResultActivity::class.java)
                intent.putExtra("PATIENT_NAME", report.patientName)
                intent.putExtra("PATIENT_GENDER", report.patientGender)
                intent.putExtra("LESION_SIZE", report.lesionSize)
                intent.putExtra("ABNORMALITY_SCORE", report.abnormalityScore)
                intent.putExtra("DENSITY_SCORE", report.densityScore)
                intent.putExtra("INFLAMMATORY_MARKER", report.inflammatoryMarker)
                intent.putExtra("BIOMARKER_INDEX", report.biomarkerIndex)
                intent.putExtra("CRP_LEVEL", report.crpLevel)
                intent.putExtra("DYSPLASIA_GRADE", report.dysplasiaGrade)
                intent.putExtra("KI67_INDEX", report.ki67Index)
                intent.putExtra("P53_EXPRESSION", report.p53Expression)
                intent.putExtra("EGFR_EXPRESSION", report.egfrExpression)
                intent.putExtra("SMOKING_HISTORY", report.smokingHistory)
                intent.putExtra("TOBACCO_FREQUENCY", report.tobaccoFreq)
                intent.putExtra("ALCOHOL_CONSUMPTION", report.alcoholCons)
                intent.putExtra("FAMILY_HISTORY", report.familyHistory)
                
                // Set a flag to prevent re-saving the report in AnalysisResultActivity
                intent.putExtra("IS_VIEWING_HISTORY", true)
                intent.putExtra("REPORT_ID", report.id)
                
                startActivity(intent)
            }

            container.addView(view)
        }
    }
}
