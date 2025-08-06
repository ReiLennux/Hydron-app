package com.undefined.hydron.domain.managers

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.undefined.hydron.core.Constants
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
    suspend fun processSensorData(userInfo: UserInfo, sensorData: Map<String, SensorData>, location: Location) {
        val uid = getCurrentUid() ?: return

        saveSensorDataToRoom(sensorData)

        val conditions = getUserProfileData()

        val riskAnalysis = riskAnalyzer.analyzeDehydrationRisk(
            sensorData = sensorData,
            age = userInfo.age,
            conditions = conditions
        )

        if (riskAnalysis.hasRisk) {

            val usrInfo = UserInfo(
                userId = uid,
                userName = userInfo.userName,
                isActive = true,
                age = userInfo.age,
                riskLevel = riskAnalysis.riskLevel,
                gender = userInfo.gender
            )

            val riskPayload = TrackingPayload(
                userInfo = usrInfo,
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

            val emergencyPayload = TrackingPayload(
                location = location,
                sensorData = null,
                userInfo = null,
                riskLevel = risk.riskLevel,
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

    // 6. FINALIZAR SESIÓN
    suspend fun endMonitoringSession() {
        try {
            val uid = getCurrentUid() ?: return

            locationSyncJob?.cancel()

            stopLocationTracking()

            // Enviar solo isActive = false
            val endSessionPayload = TrackingPayload(
                userInfo = UserInfo(
                    userId = uid,
                    isActive = false
                )
            )

            val response = bindDataUseCases.bindData(endSessionPayload)
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
                    isUploaded = false
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error guardando en Room: ${e.message}")
        }
    }

    private fun getCurrentUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    private suspend fun getUserProfileData(): List<String> {

        val condiciones = mutableListOf<String>()

        if (dataStoreUseCases.getDataBoolean(Constants.USER_DIABETES)) {
            condiciones.add("diabetes")
        }
        if (dataStoreUseCases.getDataBoolean(Constants.USER_HEART_DISEASE)) {
            condiciones.add("cardiopatia")
        }
        if (dataStoreUseCases.getDataBoolean(Constants.USER_HYPERTENSION)) {
            condiciones.add("hipertension")
        }

        val enfermedadesCronicas = dataStoreUseCases.getDataString(Constants.USER_CHRONIC_DISEASE_DETAILS)
        if (enfermedadesCronicas.isNotBlank()) {
            condiciones.addAll(enfermedadesCronicas.split(",").map { it.trim().lowercase() })
        }

        return condiciones
    }


}