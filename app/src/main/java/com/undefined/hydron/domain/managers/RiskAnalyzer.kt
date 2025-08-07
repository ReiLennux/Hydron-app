package com.undefined.hydron.domain.managers

import android.util.Log
import com.undefined.hydron.domain.models.RiskAnalysis
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import com.undefined.hydron.infrastructure.db.preloaded.FrecuenciaCardiacaDataSet
import com.undefined.hydron.infrastructure.db.preloaded.PresionArterialDataSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiskAnalyzer @Inject constructor() {

    fun analyzeDehydrationRisk(
        sensorData: List<SensorData>,
        age: Int?,
        conditions: List<String>
    ): RiskAnalysis {
        try {
            val issues = mutableListOf<String>()
            var riskScore = 0.0

            Log.d("RiskAnalyzer", "=== INICIO ANÁLISIS ===")
            Log.d("RiskAnalyzer", "Edad: $age")
            Log.d("RiskAnalyzer", "Condiciones: $conditions")
            Log.d("RiskAnalyzer", "Datos sensores: ${sensorData.map { "${it.sensorType}=${it.value}" }}")

            if (age == null) {
                Log.w("RiskAnalyzer", "Edad no proporcionada, usando valores generales")
                return RiskAnalysis(false, 0.0, "Datos insuficientes para análisis")
            }

            // Obtener perfiles
            val fcProfile = FrecuenciaCardiacaDataSet.find { age in it.edadMin..it.edadMax }

            val estadoSalud = when {
                conditions.any { it.contains("diabetes", ignoreCase = true) } -> "Diabetes"
                conditions.any { it.contains("erc", ignoreCase = true) } -> "ERC"
                conditions.any { it.contains("fq", ignoreCase = true) } -> "FQ"
                conditions.any { it.contains("hipertension", ignoreCase = true) } -> "Crónica"
                else -> "Saludable"
            }

            val presionProfile = PresionArterialDataSet.find {
                age in it.edadMin..it.edadMax && it.estadoSalud.equals(estadoSalud, ignoreCase = true)
            }

            Log.d("RiskAnalyzer", "Perfiles encontrados - FC: ${fcProfile != null}, Presión: ${presionProfile != null}")
            Log.d("RiskAnalyzer", "Estado salud detectado: $estadoSalud")

            // Extraer últimos valores únicos de sensores simples
            val heartRate = sensorData.lastOrNull { it.sensorType == SensorType.HEART_RATE }?.value
            val skinTemp = sensorData.lastOrNull { it.sensorType == SensorType.TEMPERATURE }?.value

            // Análisis de STEP_COUNT como series
            val stepSeries = sensorData
                .filter { it.sensorType == SensorType.STEP_COUNT }
                .sortedBy { it.takenAt }

            val stepDeltas = stepSeries.zipWithNext { a, b -> b.value - a.value }
            val avgDelta = stepDeltas.takeIf { it.isNotEmpty() }?.average()

            val inferredActivity = when {
                avgDelta == null -> null
                avgDelta < 1.0 -> "reposo"
                avgDelta < 10.0 -> "caminar lento"
                avgDelta < 30.0 -> "caminar normal"
                else -> "actividad intensa"
            }

            if (inferredActivity != null) {
                Log.d("RiskAnalyzer", "Actividad inferida por pasos: $inferredActivity (Δpromedio = ${avgDelta?.toInt()})")
            }

            // 1. FC
            if (heartRate != null && heartRate > 0) {
                val fcReposoMax = fcProfile?.fcReposoMax ?: 100
                if (heartRate > fcReposoMax) {
                    riskScore += 0.3
                    issues.add("Frecuencia cardíaca elevada ($heartRate > $fcReposoMax)")
                }
            }

            // 2. Temperatura
            if (skinTemp != null && skinTemp > 0) {
                if (skinTemp > 37.5) {
                    riskScore += 0.4
                    issues.add("Temperatura corporal alta ($skinTemp°C)")
                }
            }

            // 3. Interpretar riesgo según actividad detectada
            if (inferredActivity != null) {
                when (inferredActivity) {
                    "reposo" -> {
                        if (skinTemp != null && skinTemp > 37.5) {
                            riskScore += 0.2
                            issues.add("Temperatura elevada en reposo")
                        }
                        if (heartRate != null && heartRate > (fcProfile?.fcReposoMax ?: 100)) {
                            riskScore += 0.1
                            issues.add("Frecuencia cardíaca elevada en reposo")
                        }
                    }

                    "actividad intensa" -> {
                        if (heartRate != null && heartRate > (fcProfile?.fcReposoMax ?: 100)) {
                            riskScore += 0.2
                            issues.add("Ejercicio intenso con FC alta")
                        }
                    }
                }
            }

            riskScore = riskScore.coerceIn(0.0, 1.0)
            val hasRisk = riskScore > 0.4

            return RiskAnalysis(
                hasRisk = hasRisk,
                riskLevel = riskScore,
                description = if (hasRisk) issues.joinToString(", ") else "Sin riesgo detectado",
                recommendedActions = if (hasRisk) getRecommendations(riskScore) else emptyList()
            )

        } catch (e: Exception) {
            Log.e("RiskAnalyzer", "Error analizando riesgo: ${e.message}", e)
            return RiskAnalysis(false, 0.0, "Error en análisis: ${e.message}")
        }
    }

    private fun getRecommendations(riskScore: Double): List<String> {
        return when {
            riskScore > 0.8 -> listOf(
                "Buscar sombra inmediatamente",
                "Beber agua abundante",
                "Contactar servicios de emergencia",
                "Suspender actividad física"
            )
            riskScore > 0.6 -> listOf(
                "Reducir actividad física inmediatamente",
                "Hidratarse frecuentemente",
                "Buscar lugar fresco y ventilado",
                "Monitorear síntomas constantemente"
            )
            riskScore > 0.4 -> listOf(
                "Beber agua regularmente",
                "Monitorear síntomas de deshidratación",
                "Evitar exposición solar prolongada"
            )
            else -> emptyList()
        }
    }
}
