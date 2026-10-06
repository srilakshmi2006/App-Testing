package com.example.oralpred

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AnalyzeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)
        val navAnalyze = findViewById<LinearLayout>(R.id.navAnalyze)
        val navReports = findViewById<LinearLayout>(R.id.navReports)
        val navSettings = findViewById<LinearLayout>(R.id.navSettings)
        val analyzeReportBtn = findViewById<Button>(R.id.analyzeReportBtn)

        val etPatientName = findViewById<android.widget.EditText>(R.id.etPatientName)
        val etLesionSize = findViewById<android.widget.EditText>(R.id.etLesionSize)
        val etAbnormalityScore = findViewById<android.widget.EditText>(R.id.etAbnormalityScore)
        val etDensityScore = findViewById<android.widget.EditText>(R.id.etDensityScore)
        val etInflammatoryMarker = findViewById<android.widget.EditText>(R.id.etInflammatoryMarker)
        val etBiomarkerIndex = findViewById<android.widget.EditText>(R.id.etBiomarkerIndex)
        val etCrpLevel = findViewById<android.widget.EditText>(R.id.etCrpLevel)
        val etDysplasiaGrade = findViewById<android.widget.EditText>(R.id.etDysplasiaGrade)
        val etKi67Index = findViewById<android.widget.EditText>(R.id.etKi67Index)
        val etP53Expression = findViewById<android.widget.EditText>(R.id.etP53Expression)
        val etEgfrExpression = findViewById<android.widget.EditText>(R.id.etEgfrExpression)

        val spGender = findViewById<android.widget.Spinner>(R.id.spGender)
        val spSmokingHistory = findViewById<android.widget.Spinner>(R.id.spSmokingHistory)
        val spTobaccoFrequency = findViewById<android.widget.Spinner>(R.id.spTobaccoFrequency)
        val spAlcoholConsumption = findViewById<android.widget.Spinner>(R.id.spAlcoholConsumption)
        val spFamilyHistory = findViewById<android.widget.Spinner>(R.id.spFamilyHistory)

        backBtn.setOnClickListener {
            finish() // Return to previous screen
        }

        navDashboard.setOnClickListener {
            // Navigate back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        navAnalyze.setOnClickListener {
            // Already here
        }

        navReports.setOnClickListener {
            val intent = Intent(this, ReportsActivity::class.java)
            startActivity(intent)
            finish()
        }

        navSettings?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        analyzeReportBtn.setOnClickListener {
            val intent = Intent(this, AnalysisResultActivity::class.java)
            intent.putExtra("PATIENT_NAME", etPatientName?.text?.toString() ?: "")
            intent.putExtra("PATIENT_GENDER", spGender?.selectedItem?.toString() ?: "Select")
            intent.putExtra("LESION_SIZE", etLesionSize?.text?.toString()?.toFloatOrNull() ?: 0f)
            intent.putExtra("ABNORMALITY_SCORE", etAbnormalityScore?.text?.toString()?.toFloatOrNull() ?: 0f)
            intent.putExtra("DENSITY_SCORE", etDensityScore?.text?.toString()?.toFloatOrNull() ?: 0f)
            intent.putExtra("INFLAMMATORY_MARKER", etInflammatoryMarker?.text?.toString()?.toFloatOrNull() ?: 0f)
            intent.putExtra("BIOMARKER_INDEX", etBiomarkerIndex?.text?.toString()?.toFloatOrNull() ?: 0f)
            
            intent.putExtra("CRP_LEVEL", etCrpLevel?.text?.toString()?.toFloatOrNull() ?: 0f)
            intent.putExtra("DYSPLASIA_GRADE", etDysplasiaGrade?.text?.toString()?.toFloatOrNull() ?: 0f)
            intent.putExtra("KI67_INDEX", etKi67Index?.text?.toString()?.toFloatOrNull() ?: 0f)
            intent.putExtra("P53_EXPRESSION", etP53Expression?.text?.toString()?.toFloatOrNull() ?: 0f)
            intent.putExtra("EGFR_EXPRESSION", etEgfrExpression?.text?.toString()?.toFloatOrNull() ?: 0f)
            
            intent.putExtra("SMOKING_HISTORY", if (spSmokingHistory?.selectedItemPosition == 1) 1f else 0f)
            
            val tobaccoVal = when (spTobaccoFrequency?.selectedItemPosition) {
                2 -> 1f
                1 -> 0.5f
                else -> 0f
            }
            intent.putExtra("TOBACCO_FREQUENCY", tobaccoVal)
            
            val alcoholVal = when (spAlcoholConsumption?.selectedItemPosition) {
                2 -> 1f
                1 -> 0.5f
                else -> 0f
            }
            intent.putExtra("ALCOHOL_CONSUMPTION", alcoholVal)
            
            intent.putExtra("FAMILY_HISTORY", if (spFamilyHistory?.selectedItemPosition == 1) 1f else 0f)
            
            startActivity(intent)
        }
    }
}
