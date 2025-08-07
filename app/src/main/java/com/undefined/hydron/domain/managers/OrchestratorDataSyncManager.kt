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
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrchestratorDataSyncManager @Inject constructor (
    private val dataStoreUseCases: DataStoreUseCases,
    private val riskAnalyzer: RiskAnalyzer,
    private val bindDataUseCases: BindDataUseCases,
    private val locationProvider: ILocationProvider
) {

    private var locationSyncJob: Job? = null
    private var lastRiskLevel: Double = 0.0
    private var lastRiskSentTime: Long = 0L

    companion object {
        private const val TAG = "DataSyncManager"
    }

    // 1. INICIO DE SESIÓN - Envío inmediato de UserInfo
    suspend fun startMonitoringSession(userInfo: UserInfo, initialLocation: Location) {
        Log.d(TAG, "=== INICIANDO SESIÓN DE MONITOREO ===")

        val initialPayload = TrackingPayload(
            userInfo = userInfo.copy(isActive = true, riskLevel = 0.0),
            location = initialLocation,
            sensorData = null
        )

        startLocationTracking()

        val response = bindDataUseCases.bindData(initialPayload)
        when (response) {
            is Response.Success -> {
                Log.d(TAG, "Sesión iniciada correctamente para usuario: ${userInfo.userId}")
                lastRiskLevel = 0.0
                lastRiskSentTime = System.currentTimeMillis()
            }
            is Response.Error -> {
                Log.e(TAG, "Error iniciando sesión: ${response.exception?.message}")
            }

            Response.Idle -> {}
            Response.Loading -> {}
        }
    }

    // 2. ENVÍO PERIÓDICO DE UBICACIÓN (cada 5 minutos o por movimiento significativo)
    private fun startLocationTracking() {
        locationProvider.startLocationUpdates(minDistanceMeters = 10f) { newLocation ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    sendLocationUpdate(newLocation)
                } catch (e: Exception) {
                    Log.e(TAG, "Error en tracking de ubicación: ${e.message}")
                }
            }
        }
    }

    private suspend fun sendLocationUpdate(location: Location) {
        val payload = TrackingPayload(
            userInfo = null,
            location = location,
            sensorData = null
        )

        val response = bindDataUseCases.bindData(payload)
        when (response) {
            is Response.Success -> {
                Log.d(TAG, "Ubicación enviada: lat=${location.latitude}, lon=${location.longitude}")
            }
            is Response.Error -> {
                Log.w(TAG, "Error al enviar ubicación: ${response.exception?.message}")
            }

            Response.Idle -> {}
            Response.Loading -> {}
        }
    }

    private fun stopLocationTracking() {
        locationProvider.stopLocationUpdates()
        Log.d(TAG, "Tracking de ubicación detenido")
    }

    // 3. ANÁLISIS Y PROCESAMIENTO DE DATOS DE SENSORES
    suspend fun processSensorData(userInfo: UserInfo, sensorData: List<SensorData>, location: Location) {
        val uid = getCurrentUid() ?: return

        try {

            val conditions = getUserProfileData()

            val riskAnalysis = riskAnalyzer.analyzeDehydrationRisk(sensorData, userInfo.age, conditions)

                sendRiskData(userInfo, sensorData, location, riskAnalysis, uid)

        } catch (e: Exception) {
            Log.e(TAG, "Error procesando datos de sensores: ${e.message}")
        }
    }

    private suspend fun sendRiskData(
        userInfo: UserInfo,
        sensorData: List<SensorData>,
        location: Location,
        riskAnalysis: RiskAnalysis,
        uid: String
    ) {
        try {
            val updatedUserInfo = userInfo.copy(
                userId = uid,
                isActive = true,
                riskLevel = riskAnalysis.riskLevel
            )

            val sensorMap = sensorData.associateBy { it.id.toString() }


            val riskPayload = TrackingPayload(
                userInfo = updatedUserInfo,
                location = location,
                sensorData = sensorMap
            )

            val response = bindDataUseCases.bindData(riskPayload)
            when (response) {
                is Response.Success -> {
                    Log.d(TAG, "Datos de riesgo enviados exitosamente - Nivel: ${riskAnalysis.riskLevel}")

                }
                is Response.Error -> {
                    Log.e(TAG, "Error enviando datos de riesgo: ${response.exception?.message}")
                }

                Response.Idle -> {}
                Response.Loading -> {}
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en sendRiskData: ${e.message}")
        }
    }

    // 4. ALERTA DE EMERGENCIA
    private suspend fun sendEmergencyAlert(uid: String, risk: RiskAnalysis, location: Location) {
        try {

            val emergencyPayload = TrackingPayload(
                userInfo = UserInfo(
                    userId = uid,
                    isActive = true,
                    riskLevel = risk.riskLevel
                ),
                location = location,
                sensorData = null,
            )

            val response = bindDataUseCases.bindData(emergencyPayload)

        } catch (e: Exception) {
            Log.e(TAG, "Error crítico enviando alerta: ${e.message}")
        }
    }

    // 5. FINALIZAR SESIÓN
    suspend fun endMonitoringSession() {
        try {
            val uid = getCurrentUid() ?: return

            // Cancelar jobs activos
            locationSyncJob?.cancel()
            stopLocationTracking()

            // Enviar señal de fin de sesión
            val endSessionPayload = TrackingPayload(
                userInfo = UserInfo(
                    userId = uid,
                    isActive = false,
                    riskLevel = 0.0 // Reset del riesgo al finalizar
                ),
                location = null,
                sensorData = null
            )

            val response = bindDataUseCases.bindData(endSessionPayload)
            when (response) {
                is Response.Success -> {
                    Log.d(TAG, " Sesión finalizada correctamente para: $uid")
                }
                is Response.Error -> {
                    Log.e(TAG, " Error finalizando sesión: ${response.exception?.message}")
                }

                Response.Idle -> {}
                Response.Loading -> {}
            }

            resetLocalState()

        } catch (e: Exception) {
            Log.e(TAG, "Error finalizando sesión: ${e.message}")
        }
    }

    //Handlers
    private suspend fun resetLocalState() {
        dataStoreUseCases.setDataBoolean(KEY_IS_MONITORING_TOGGLE, false)
        lastRiskLevel = 0.0
        lastRiskSentTime = 0L
        Log.d(TAG, "Estado local limpiado")
    }

    private fun getCurrentUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid?.also {
            Log.v(TAG, "Usuario actual: $it")
        }
    }

    private suspend fun getUserProfileData(): List<String> {
        val condiciones = mutableListOf<String>()

        try {
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
                condiciones.addAll(
                    enfermedadesCronicas.split(",")
                        .map { it.trim().lowercase() }
                        .filter { it.isNotBlank() }
                )
            }

            Log.d(TAG, "📋 Condiciones del usuario: $condiciones")
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo perfil del usuario: ${e.message}")
        }

        return condiciones
    }
}