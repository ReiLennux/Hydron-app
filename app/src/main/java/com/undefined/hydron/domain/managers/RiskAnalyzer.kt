package com.undefined.hydron.domain.managers

import android.util.Log
import com.undefined.hydron.domain.models.RiskAnalysis
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.infrastructure.db.preloaded.ActividadFisicaDataSet
import com.undefined.hydron.infrastructure.db.preloaded.FrecuenciaCardiacaDataSet
import com.undefined.hydron.infrastructure.db.preloaded.PresionArterialDataSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiskAnalyzer @Inject constructor(

) {

    fun analyzeDehydrationRisk(
        sensorData: Map<String, SensorData>,
        age: Int?,
        conditions: List<String>
    ): RiskAnalysis {
        try {

            val issues = mutableListOf<String>()
            var riskScore = 0.0

            val heartRate = sensorData["heart_rate"]?.value ?: 0.0
            val skinTemp = sensorData["skin_temperature"]?.value ?: 0.0
            val activityLevel = sensorData["activity_level"]?.value ?: 0.0
            val systolic = sensorData["blood_pressure_systolic"]?.value ?: 0.0
            val diastolic = sensorData["blood_pressure_diastolic"]?.value ?: 0.0

            // Obtener perfil FC
            val fcProfile = FrecuenciaCardiacaDataSet.find { age != null && age in it.edadMin..it.edadMax }

            // Obtener perfil actividad física para condición principal (o "General")
            val mainCondition = conditions.firstOrNull()?.capitalize() ?: "General"
            val actividadProfile = ActividadFisicaDataSet.find { age != null && age in it.edadMin..it.edadMax && it.grupo == mainCondition }
                ?: ActividadFisicaDataSet.find { age != null && age in it.edadMin..it.edadMax && it.grupo == "General" }

            // Obtener perfil presión arterial según condición y edad
            val estadoSalud = when {
                conditions.any { it.contains("diabetes") } -> "Diabetes"
                conditions.any { it.contains("erc") } -> "ERC"
                conditions.any { it.contains("fq") } -> "FQ"
                conditions.any { it.contains("hipertension") } -> "Crónica"
                else -> "Saludable"
            }
            val presionProfile = PresionArterialDataSet.find { age != null && age in it.edadMin..it.edadMax && it.estadoSalud.equals(estadoSalud, ignoreCase = true) }

            // 1. Analizar frecuencia cardíaca
            val fcMax = fcProfile?.fcMax ?: 180
            val fcReposoMax = fcProfile?.fcReposoMax ?: 100

            if (heartRate > fcReposoMax) {
                riskScore += 0.3
                issues.add("Frecuencia cardíaca elevada")
            }

            // 2. Analizar temperatura corporal
            if (skinTemp > 37.5) {
                riskScore += 0.4
                issues.add("Temperatura corporal alta")
            }

            // 3. Comparar actividad física real vs recomendada
            if (actividadProfile != null) {
                val horasRecomendadas = actividadProfile.horasPorSemana
                val horasReal = activityLevel * 14 // Ejemplo: si activityLevel es 0..1 y escalas a horas aprox semanales
                if (horasReal < horasRecomendadas) {
                    riskScore += 0.15
                    issues.add("Actividad física menor a la recomendada para su condición")
                }
            }

            // 4. Evaluar presión arterial
            if (presionProfile != null) {
                if (systolic < presionProfile.sistolicaMin || systolic > presionProfile.sistolicaMax) {
                    riskScore += 0.2
                    issues.add("Presión sistólica fuera de rango (${systolic.toInt()})")
                }
                if (diastolic < presionProfile.diastolicaMin || diastolic > presionProfile.diastolicaMax) {
                    riskScore += 0.2
                    issues.add("Presión diastólica fuera de rango (${diastolic.toInt()})")
                }
            }

            // 5. Otras condiciones o sensores, sumar riesgo adicional si se requiere

            riskScore = riskScore.coerceIn(0.0, 1.0)
            val hasRisk = riskScore > 0.4

            return RiskAnalysis(
                hasRisk = hasRisk,
                riskLevel = riskScore,
                description = if (hasRisk) issues.joinToString(", ") else "Sin riesgo detectado",
                recommendedActions = if (hasRisk) getRecommendations(riskScore) else emptyList()
            )


        } catch (e: Exception) {
            Log.e("RiskAnalyzer", "Error analizando riesgo: ${e.message}")
            return RiskAnalysis(false, 0.0, "Error en análisis")
        }
    }

    private fun getRecommendations(riskScore: Double): List<String> {
        return when {
            riskScore > 0.8 -> listOf(
                "Buscar sombra inmediatamente",
                "Beber agua",
                "Contactar emergencias"
            )
            riskScore > 0.6 -> listOf("Reducir actividad", "Hidratarse", "Buscar lugar fresco")
            riskScore > 0.4 -> listOf("Beber agua", "Monitorear síntomas")
            else -> emptyList()
        }
    }
}
