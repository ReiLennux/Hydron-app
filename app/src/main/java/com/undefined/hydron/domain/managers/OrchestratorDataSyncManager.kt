package com.undefined.hydron.domain.managers

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.undefined.hydron.core.Constants.KEY_IS_MONITORING_TOGGLE
import com.undefined.hydron.domain.interfaces.ILocationProvider
import com.undefined.hydron.domain.models.*
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.useCases.bindData.BindDataUseCases
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrchestratorDataSyncManager @Inject constructor (
    private val dataStoreUseCases: DataStoreUseCases,
    private val sensorDataUseCases: SensorDataUseCases,
    private val riskAnalyzer: RiskAnalyzer,
    private val bindDataUseCases: BindDataUseCases,
    private val locationProvider: ILocationProvider

) {

    private var locationSyncJob: Job? = null

    companion object {
        private const val TAG = "DataSyncManager"
    }

    // 1. INICIO DE SESIÓN - Envío inmediato de UserInfo
    suspend fun startMonitoringSession(userInfo: UserInfo, initialLocation: Location) {
        val initialPayload = TrackingPayload(
            userInfo = userInfo,
            location = initialLocation,
            sensorData = null
        )

        startLocationTracking()

        val response = bindDataUseCases.bindData(initialPayload)
        if (response is Response.Success) {
            Log.d(TAG, "Sesión iniciada correctamente")
        } else if (response is Response.Error) {
            Log.e(TAG, "Error iniciando sesión: ${response.exception?.message}")
        }
    }


    // 2. ENVÍO PERIÓDICO DE UBICACIÓN (cada 5 minutos)
    private fun startLocationTracking() {
        locationProvider.startLocationUpdates(minDistanceMeters = 5f) { newLocation ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val payload = TrackingPayload(
                        userInfo = null,
                        location = newLocation,
                        sensorData = null
                    )
                    val response = bindDataUseCases.bindData(payload)
                    if (response is Response.Success) {
                        Log.d(TAG, "Ubicación enviada por movimiento")
                    } else if (response is Response.Error) {
                        Log.e(TAG, "Error al enviar ubicación: ${response.exception?.message}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error tracking ubicación: ${e.message}")
                }
            }
        }
    }

    private fun stopLocationTracking() {
        locationProvider.stopLocationUpdates()
    }


    // 3. ANÁLISIS DE RIESGO - Solo enviar si hay riesgo detectado
    suspend fun processSensorData(sensorData: Map<String, SensorData>, location: Location) {
        val uid = getCurrentUid() ?: return

        saveSensorDataToRoom(sensorData)

        val riskAnalysis = riskAnalyzer.analyzeDehydrationRisk(sensorData)

        if (riskAnalysis.hasRisk) {
            val riskPayload = TrackingPayload(
                userInfo = null,
                location = location,
                sensorData = sensorData
            )

            val response = bindDataUseCases.bindData(riskPayload)
            if (response is Response.Error) {
                Log.e(TAG, "Error enviando datos de riesgo: ${response.exception?.message}")
            }

            if (riskAnalysis.riskLevel >= 0.7) {
                sendEmergencyAlert(uid, riskAnalysis, location)
            }
        }
    }


    // 4. ALERTA DE EMERGENCIA
    private suspend fun sendEmergencyAlert(uid: String, risk: RiskAnalysis, location: Location) {
        try {
            // Determinar el estado de hidratación basado en el riesgo
            val hydrationStatus = when {
                risk.riskLevel >= 0.7 -> HydrationStatus.LOW
                risk.riskLevel >= 0.4 -> HydrationStatus.MEDIUM
                else -> HydrationStatus.HIGH
            }

            val emergencyPayload = TrackingPayload(
                location = location,
                sensorData = null,
                userInfo = null,
                riskLevel = (risk.riskLevel * 100).toInt(), // Convertir a entero (0-100)
                hydrationStatus = hydrationStatus
            )

            val response = bindDataUseCases.bindData(emergencyPayload)
            if (response is Response.Success) {
                Log.e(TAG, "ALERTA DE EMERGENCIA ENVIADA - Riesgo: ${risk.riskLevel}")
            } else if (response is Response.Error) {
                Log.e(TAG, "Error enviando alerta: ${response.exception?.message}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error enviando alerta: ${e.message}")
        }
    }

    suspend fun performBatchUpload(): BatchUploadResult {
        return try {

            // Obtener datos no enviados desde Room
            val pendingSensorData = sensorDataUseCases.getPendingSensorData()

            if (pendingSensorData.isEmpty()) {
                return BatchUploadResult(
                    success = true,
                    uploadedCount = 0,
                    message = "No hay datos pendientes"
                )
            }

            // Agrupar datos por timestamp para crear batches
            val batchData = groupSensorDataForBatch(pendingSensorData)

            var uploadedCount = 0
            batchData.forEach { (timestamp, sensors) ->
                val sensorDataMap = sensors.associateBy { it.id.toString() }

                val batchPayload = TrackingPayload(
                    userInfo = null,
                    location = null,
                    sensorData = sensorDataMap
                )

                val response = bindDataUseCases.bindData(batchPayload)
                if (response is Response.Success) {
                    uploadedCount++
                } else if (response is Response.Error) {
                    Log.e(TAG, "Error en batch $timestamp: ${response.exception?.message}")
                }
            }

            // Marcar como enviados en Room
            sensorDataUseCases.markAsUploaded(pendingSensorData.map { it.id })

            Log.d(TAG, "Batch upload completado: $uploadedCount batches")

            BatchUploadResult(
                success = true,
                uploadedCount = uploadedCount,
                message = "Datos enviados exitosamente"
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error en batch upload: ${e.message}")
            BatchUploadResult(
                success = false,
                uploadedCount = 0,
                message = "Error: ${e.message}"
            )
        }
    }

    // 6. FINALIZAR SESIÓN
    suspend fun endMonitoringSession() {
        try {
            val uid = getCurrentUid() ?: return

            // Cancelar sincronización de ubicación
            locationSyncJob?.cancel()

            stopLocationTracking()

            // Enviar solo isActive = false
            val endSessionPayload = TrackingPayload(
                userInfo = UserInfo(
                    userId = uid,
                    isActive = false
                )
            )

            val response = bindDataUseCases.bindData(endSessionPayload)  // <-- AQUÍ ESTÁ
            if (response is Response.Success) {
                Log.d(TAG, "Sesión finalizada correctamente: $uid")
            } else if (response is Response.Error) {
                Log.e(TAG, "Error finalizando sesión: ${response.exception?.message}")
            }

            // Limpiar estado local
            dataStoreUseCases.setDataBoolean(KEY_IS_MONITORING_TOGGLE, false)

        } catch (e: Exception) {
            Log.e(TAG, "Error finalizando sesión: ${e.message}")
        }
    }

    // MÉTODOS AUXILIARES
    private suspend fun saveSensorDataToRoom(sensorData: Map<String, SensorData>) {
        try {
            sensorData.values.forEach { sensor ->
                sensorDataUseCases.addSensorData(sensor.copy(
                    takenAt = System.currentTimeMillis(),
                    isUploaded = false // Marcar como no enviado
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error guardando en Room: ${e.message}")
        }
    }

    private fun groupSensorDataForBatch(sensorData: List<SensorData>): Map<Long, List<SensorData>> {
        // Agruper por ventanas de tiempo (ej: cada 10 minutos)
        val windowSize = 10 * 60 * 1000L // 10 minutos
        return sensorData.groupBy { it.takenAt / windowSize * windowSize }
    }

    private fun getCurrentUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

}

// MODELOS DE SOPORTE
data class RiskAnalysis(
    val hasRisk: Boolean,
    val riskLevel: Double, // 0.0 - 1.0
    val description: String,
    val recommendedActions: List<String> = emptyList()
)

data class BatchUploadResult(
    val success: Boolean,
    val uploadedCount: Int,
    val message: String
)

// ANALIZADOR DE RIESGO
@Singleton
class RiskAnalyzer @Inject constructor() {

    fun analyzeDehydrationRisk(sensorData: Map<String, SensorData>): RiskAnalysis {
        try {
            // Aquí implementarías tu lógica de análisis
            // Ejemplo simplificado:

            val heartRate = sensorData["heart_rate"]?.value ?: 0.0
            val skinTemp = sensorData["skin_temperature"]?.value ?: 0.0
            val activity = sensorData["activity_level"]?.value ?: 0.0

            var riskScore = 0.0
            val issues = mutableListOf<String>()

            // Análisis de frecuencia cardíaca
            if (heartRate > 100) {
                riskScore += 0.3
                issues.add("Frecuencia cardíaca elevada")
            }

            // Análisis de temperatura
            if (skinTemp > 37.5) {
                riskScore += 0.4
                issues.add("Temperatura corporal alta")
            }

            // Análisis de actividad vs condiciones
            if (activity > 0.7 && skinTemp > 35.0) {
                riskScore += 0.3
                issues.add("Alta actividad en condiciones calurosas")
            }

            val hasRisk = riskScore > 0.4 // Umbral de riesgo

            return RiskAnalysis(
                hasRisk = hasRisk,
                riskLevel = riskScore.coerceIn(0.0, 1.0),
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
