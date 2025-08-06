package com.undefined.hydron.domain.models


data class RiskAnalysis(
    val hasRisk: Boolean,
    val riskLevel: Double, // 0.0 - 1.0
    val description: String,
    val recommendedActions: List<String> = emptyList()
)