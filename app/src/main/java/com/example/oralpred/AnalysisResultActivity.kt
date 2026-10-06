package com.example.oralpred

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalysisResultActivity : AppCompatActivity() {

    private lateinit var aiModel: AIModel
    private var riskPercentage: Int = 0
    private var classification: String = ""
    private var aiText: String = ""
    private var recsText: String = ""
    private var patientName: String = ""
    private var lesionSize: Float = 0f
    private var abnormalityScore: Float = 0f
    private var densityScore: Float = 0f
    private var inflammatoryMarker: Float = 0f
    private var biomarkerIndex: Float = 0f
    private var crpLevel: Float = 0f
    private var dysplasiaGrade: Float = 0f
    private var ki67Index: Float = 0f
    private var p53Expression: Float = 0f
    private var egfrExpression: Float = 0f
    private var smokingHistory: Float = 0f
    private var tobaccoFreq: Float = 0f
    private var alcoholCons: Float = 0f
    private var familyHistory: Float = 0f
    private var patientGender: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_result)

        aiModel = AIModel(this)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val riskProgressBar = findViewById<ProgressBar>(R.id.riskProgressBar)
        val riskPercentageText = findViewById<TextView>(R.id.riskPercentageText)
        val riskClassificationText = findViewById<TextView>(R.id.riskClassificationText)
        val aiPredictionText = findViewById<TextView>(R.id.aiPredictionText)
        val recommendationsText = findViewById<TextView>(R.id.recommendationsText)
        val biomarkerChart = findViewById<BarChart>(R.id.biomarkerChart)
        val downloadPdfBtn = findViewById<Button>(R.id.downloadPdfBtn)

        backBtn.setOnClickListener { finish() }

        // Get intent data
        patientName = intent.getStringExtra("PATIENT_NAME") ?: "Unknown Patient"
        patientGender = intent.getStringExtra("PATIENT_GENDER") ?: "Select"
        lesionSize = intent.getFloatExtra("LESION_SIZE", 0f)
        abnormalityScore = intent.getFloatExtra("ABNORMALITY_SCORE", 0f)
        densityScore = intent.getFloatExtra("DENSITY_SCORE", 0f)
        inflammatoryMarker = intent.getFloatExtra("INFLAMMATORY_MARKER", 0f)
        biomarkerIndex = intent.getFloatExtra("BIOMARKER_INDEX", 0f)
        crpLevel = intent.getFloatExtra("CRP_LEVEL", 0f)
        dysplasiaGrade = intent.getFloatExtra("DYSPLASIA_GRADE", 0f)
        ki67Index = intent.getFloatExtra("KI67_INDEX", 0f)
        p53Expression = intent.getFloatExtra("P53_EXPRESSION", 0f)
        egfrExpression = intent.getFloatExtra("EGFR_EXPRESSION", 0f)
        
        smokingHistory = intent.getFloatExtra("SMOKING_HISTORY", 0f)
        tobaccoFreq = intent.getFloatExtra("TOBACCO_FREQUENCY", 0f)
        alcoholCons = intent.getFloatExtra("ALCOHOL_CONSUMPTION", 0f)
        familyHistory = intent.getFloatExtra("FAMILY_HISTORY", 0f)

        // Run AI / Fetch Cache
        riskPercentage = aiModel.predictRisk(
            lesionSize, abnormalityScore, densityScore, inflammatoryMarker, biomarkerIndex,
            crpLevel, dysplasiaGrade, ki67Index, p53Expression, egfrExpression,
            smokingHistory, tobaccoFreq, alcoholCons, familyHistory, patientGender
        )

        // Determine Classification
        var classificationColor = Color.parseColor("#10B981")
        classification = "Healthy"
        aiText = "Patient shows normal biomarker levels. No immediate signs of risk detected."
        recsText = "• Maintain regular dental checkups.\n• Continue healthy lifestyle habits."

        if (riskPercentage > 60) {
            classification = "High Risk"
            classificationColor = Color.parseColor("#EF4444")
            aiText = "Patient shows severely elevated biomarker levels (Index: $biomarkerIndex, Abnormality: $abnormalityScore) indicating high probability of early-stage oral cancer risk."
            recsText = "• URGENT: Visit oncologist immediately.\n• Stop all tobacco usage.\n• Schedule a biopsy."
        } else if (riskPercentage > 30) {
            classification = "Moderate Risk"
            classificationColor = Color.parseColor("#F59E0B")
            aiText = "Patient shows moderately elevated inflammatory markers ($inflammatoryMarker mg/L) and some cellular abnormality. Monitor closely."
            recsText = "• Schedule follow-up in 30 days.\n• Reduce alcohol and tobacco consumption.\n• Recommend lifestyle modifications."
        }

        // Update UI
        riskProgressBar.progress = riskPercentage
        riskPercentageText.text = "$riskPercentage%"
        riskClassificationText.text = classification
        riskClassificationText.setTextColor(classificationColor)
        aiPredictionText.text = aiText
        recommendationsText.text = recsText

        // Save Report to SharedPreferences if it's a new analysis
        val isViewingHistory = intent.getBooleanExtra("IS_VIEWING_HISTORY", false)
        if (!isViewingHistory) {
            val hexColor = String.format("#%06X", (0xFFFFFF and classificationColor))
            val currentStage = if (riskPercentage > 60) "Stage 2/3" else if (riskPercentage > 30) "Stage 1" else "None"
            val report = Report(
                id = System.currentTimeMillis().toString(),
                date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
                patientName = patientName,
                riskPercentage = riskPercentage,
                classification = classification,
                classificationColorHex = hexColor,
                stage = currentStage,
                lesionSize = lesionSize,
                abnormalityScore = abnormalityScore,
                densityScore = densityScore,
                inflammatoryMarker = inflammatoryMarker,
                biomarkerIndex = biomarkerIndex,
                crpLevel = crpLevel,
                dysplasiaGrade = dysplasiaGrade,
                ki67Index = ki67Index,
                p53Expression = p53Expression,
                egfrExpression = egfrExpression,
                smokingHistory = smokingHistory,
                tobaccoFreq = tobaccoFreq,
                alcoholCons = alcoholCons,
                familyHistory = familyHistory,
                patientGender = patientGender
            )
            aiModel.saveReport(report)
        }

        // Setup Chart
        setupChart(biomarkerChart, classificationColor)

        downloadPdfBtn.setOnClickListener {
            generatePdfReport(biomarkerChart)
        }

        val historyActionContainer = findViewById<android.widget.LinearLayout>(R.id.historyActionContainer)
        val shareReportBtn = findViewById<android.widget.Button>(R.id.shareReportBtn)
        val deleteReportBtn = findViewById<android.widget.Button>(R.id.deleteReportBtn)

        if (isViewingHistory) {
            historyActionContainer.visibility = android.view.View.VISIBLE
            val reportId = intent.getStringExtra("REPORT_ID")

            deleteReportBtn.setOnClickListener {
                if (reportId != null) {
                    aiModel.deleteReport(reportId)
                    android.widget.Toast.makeText(this, "Report deleted", android.widget.Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            shareReportBtn.setOnClickListener {
                val pdfFile = generatePdfReport(biomarkerChart)
                if (pdfFile != null) {
                    try {
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            this@AnalysisResultActivity,
                            "${applicationContext.packageName}.fileprovider",
                            pdfFile
                        )
                        
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND)
                        shareIntent.type = "application/pdf"
                        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri)
                        shareIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        
                        startActivity(android.content.Intent.createChooser(shareIntent, "Share Report"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        android.widget.Toast.makeText(this@AnalysisResultActivity, "Error sharing PDF", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val returnDashboardBtn = findViewById<android.widget.Button>(R.id.returnDashboardBtn)
        returnDashboardBtn.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupChart(chart: BarChart, classificationColor: Int) {
        val labels = listOf("Lesion", "Abnormality", "Dysplasia", "Ki-67", "p53", "EGFR", "Inflammatory", "CRP", "Index")
        
        val normalEntries = arrayListOf(
            BarEntry(0f, 2.0f),  
            BarEntry(1f, 3.0f),  
            BarEntry(2f, 1.0f),  
            BarEntry(3f, 15.0f), 
            BarEntry(4f, 10.0f), 
            BarEntry(5f, 20.0f), 
            BarEntry(6f, 3.0f),  
            BarEntry(7f, 5.0f),  
            BarEntry(8f, 2.0f)   
        )
        
        val patientEntries = arrayListOf(
            BarEntry(0f, lesionSize),
            BarEntry(1f, abnormalityScore),
            BarEntry(2f, dysplasiaGrade),
            BarEntry(3f, ki67Index),
            BarEntry(4f, p53Expression),
            BarEntry(5f, egfrExpression),
            BarEntry(6f, inflammatoryMarker),
            BarEntry(7f, crpLevel),
            BarEntry(8f, biomarkerIndex)
        )

        val normalDataSet = BarDataSet(normalEntries, "Normal Range (Max)")
        normalDataSet.color = Color.parseColor("#E5E7EB")

        val patientDataSet = BarDataSet(patientEntries, "Patient Levels")
        patientDataSet.color = classificationColor

        val data = BarData(normalDataSet, patientDataSet)
        data.barWidth = 0.35f
        
        chart.data = data
        chart.groupBars(-0.5f, 0.2f, 0.05f)

        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Lesion", "Abnormality", "Inflammation", "Index"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setCenterAxisLabels(true)
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1f
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 4f

        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.invalidate()
    }

    private fun generatePdfReport(chart: com.github.mikephil.charting.charts.BarChart): java.io.File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        // Title
        canvas.drawText("Oral Cancer Risk Analysis Report", 50f, 50f, paint)

        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        canvas.drawText("Date: ${dateFormat.format(Date())}", 50f, 80f, paint)
        canvas.drawText("Patient Name: $patientName", 50f, 100f, paint)

        // Results
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Risk Analysis", 50f, 150f, paint)

        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Cancer Risk Percentage: $riskPercentage%", 50f, 180f, paint)
        canvas.drawText("Classification: $classification", 50f, 200f, paint)

        // Biomarkers & Blood
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Clinical Measurements", 50f, 250f, paint)

        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Oral Lesion Size: $lesionSize mm", 50f, 280f, paint)
        canvas.drawText("Cell Abnormality Score: $abnormalityScore /10", 50f, 300f, paint)
        canvas.drawText("Tissue Density Score: $densityScore %", 50f, 320f, paint)
        canvas.drawText("Inflammatory Marker Level: $inflammatoryMarker mg/L", 50f, 340f, paint)
        canvas.drawText("Cancer Biomarker Index: $biomarkerIndex", 50f, 360f, paint)
        canvas.drawText("C-Reactive Protein (CRP): $crpLevel mg/L", 50f, 380f, paint)
        canvas.drawText("Dysplasia Grade: $dysplasiaGrade", 50f, 400f, paint)
        
        // Split into second column for more data
        canvas.drawText("Ki-67 Index: $ki67Index %", 300f, 280f, paint)
        canvas.drawText("p53 Expression: $p53Expression %", 300f, 300f, paint)
        canvas.drawText("EGFR Expression: $egfrExpression %", 300f, 320f, paint)
        canvas.drawText("Smoking History: ${if(smokingHistory > 0) "Yes" else "No"}", 300f, 340f, paint)
        canvas.drawText("Alcohol Consump.: ${if(alcoholCons > 0) "Yes" else "No"}", 300f, 360f, paint)

        // AI Output
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("AI Prediction Summary", 50f, 440f, paint)

        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        // Simple text wrap (rough implementation for prototype)
        val words = aiText.split(" ")
        var line = ""
        var yPos = 470f
        for (word in words) {
            if (paint.measureText(line + word) > 495f) {
                canvas.drawText(line, 50f, yPos, paint)
                yPos += 20f
                line = "$word "
            } else {
                line += "$word "
            }
        }
        canvas.drawText(line, 50f, yPos, paint)

        // Recommendations
        yPos += 50f
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Recommendations", 50f, yPos, paint)

        yPos += 30f
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val recLines = recsText.split("\n")
        for (rec in recLines) {
            canvas.drawText(rec, 50f, yPos, paint)
            yPos += 20f
        }

        document.finishPage(page)

        // Page 2 - Graph
        val pageInfo2 = PdfDocument.PageInfo.Builder(595, 842, 2).create()
        val page2 = document.startPage(pageInfo2)
        val canvas2 = page2.canvas

        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas2.drawText("Biomarker Graph", 50f, 50f, paint)

        try {
            val chartBitmap = chart.chartBitmap
            if (chartBitmap != null) {
                // Scale bitmap to fit width (595 - 100 margin = 495)
                val targetWidth = 495f
                val scale = targetWidth / chartBitmap.width
                val targetHeight = chartBitmap.height * scale
                val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(chartBitmap, targetWidth.toInt(), targetHeight.toInt(), true)
                canvas2.drawBitmap(scaledBitmap, 50f, 80f, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        document.finishPage(page2)

        // Save PDF
        return try {
            val file = java.io.File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), "Medical_Report_${System.currentTimeMillis()}.pdf")
            val outputStream = java.io.FileOutputStream(file)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()
            Toast.makeText(this, "PDF saved to Downloads folder", Toast.LENGTH_LONG).show()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show()
            null
        }
    }
}
