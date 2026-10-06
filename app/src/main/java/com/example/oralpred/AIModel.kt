package com.example.oralpred

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class AIModel(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("oral_pred_cache", Context.MODE_PRIVATE)

    fun predictRisk(
        lesionSize: Float,
        abnormalityScore: Float,
        densityScore: Float,
        inflammatoryMarker: Float,
        biomarkerIndex: Float,
        crpLevel: Float,
        dysplasiaGrade: Float,
        ki67Index: Float,
        p53Expression: Float,
        egfrExpression: Float,
        smokingHistory: Float,
        tobaccoFreq: Float,
        alcoholCons: Float,
        familyHistory: Float,
        patientGender: String
    ): Int {
        val cacheKey = "risk_cache_${patientGender}_${lesionSize}_${abnormalityScore}_${densityScore}_${inflammatoryMarker}_${biomarkerIndex}_${crpLevel}_${dysplasiaGrade}_${ki67Index}_${p53Expression}_${egfrExpression}_${smokingHistory}_${tobaccoFreq}_${alcoholCons}_${familyHistory}"
        
        // Check Cache
        if (prefs.contains(cacheKey)) {
            return prefs.getInt(cacheKey, 0)
        }

        // Simulating the TF graph: (Inputs * Weights) + Bias
        val wLesion = 2.0f
        val wAbnormality = 3.5f
        val wDysplasia = 4.0f
        val wKi67 = 2.0f
        val wP53 = 2.5f
        val wEgfr = 2.0f
        val wDensity = 1.0f
        val wInflammatory = 1.5f
        val wCrp = 1.5f
        val wBiomarker = 5.0f
        val wSmoking = 6.0f
        val wTobacco = 5.0f
        val wAlcohol = 2.0f
        val wFamily = 3.0f
        val wGender = 1.0f
        
        val bias = 2.0f

        val genderVal = if (patientGender == "Male") 1.0f else 0.0f

        val rawRisk = (lesionSize * wLesion) +
                      (abnormalityScore * wAbnormality) +
                      (dysplasiaGrade * wDysplasia) +
                      ((ki67Index / 100.0f) * wKi67) +
                      ((p53Expression / 100.0f) * wP53) +
                      ((egfrExpression / 100.0f) * wEgfr) +
                      ((densityScore / 10.0f) * wDensity) +
                      (inflammatoryMarker * wInflammatory) +
                      (crpLevel * wCrp) +
                      (biomarkerIndex * wBiomarker) + 
                      (smokingHistory * wSmoking) +
                      (tobaccoFreq * wTobacco) +
                      (alcoholCons * wAlcohol) +
                      (familyHistory * wFamily) +
                      (genderVal * wGender) + bias

        // Apply Sigmoid Activation Function for realistic probability curve
        // threshold = 80, scale = 20 (tunes the S-curve)
        val z = (rawRisk - 80.0) / 20.0
        val sigmoidRisk = 100.0 / (1.0 + Math.exp(-z))

        // Normalize and cap at 96%
        val riskPercentage = min(max(sigmoidRisk.roundToInt(), 0), 96)

        // Save to cache
        prefs.edit().putInt(cacheKey, riskPercentage).apply()

        return riskPercentage
    }

    fun saveReport(report: Report) {
        val currentReportsJson = prefs.getString("SAVED_REPORTS", "[]") ?: "[]"
        try {
            val reportsList: MutableList<Report> = Json.decodeFromString(currentReportsJson)
            // Add new report to beginning
            reportsList.add(0, report)
            val updatedJson = Json.encodeToString(reportsList)
            prefs.edit().putString("SAVED_REPORTS", updatedJson).apply()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback in case of parsing error
            val reportsList = mutableListOf(report)
            prefs.edit().putString("SAVED_REPORTS", Json.encodeToString(reportsList)).apply()
        }
    }

    fun getSavedReports(): List<Report> {
        val currentReportsJson = prefs.getString("SAVED_REPORTS", "[]") ?: "[]"
        return try {
            Json.decodeFromString(currentReportsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun deleteReport(reportId: String) {
        val reports = getSavedReports().toMutableList()
        reports.removeAll { it.id == reportId }
        prefs.edit().putString("SAVED_REPORTS", Json.encodeToString(reports)).apply()
    }
}
