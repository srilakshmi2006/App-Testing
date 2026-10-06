package com.example.oralpred

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: String,
    val date: String,
    val patientName: String,
    val riskPercentage: Int,
    val classification: String,
    val classificationColorHex: String,
    val stage: String,
    
    // Biomarkers and Risk factors
    val lesionSize: Float,
    val abnormalityScore: Float,
    val densityScore: Float,
    val inflammatoryMarker: Float,
    val biomarkerIndex: Float,
    val crpLevel: Float,
    val dysplasiaGrade: Float,
    val ki67Index: Float,
    val p53Expression: Float,
    val egfrExpression: Float,
    val smokingHistory: Float,
    val tobaccoFreq: Float,
    val alcoholCons: Float,
    val familyHistory: Float,
    val patientGender: String
)
