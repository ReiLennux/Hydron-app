package com.undefined.hydron.domain.managers

import android.util.Log
import com.undefined.hydron.core.Constants.KEY_IS_MONITORING_TOGGLE
import com.undefined.hydron.domain.interfaces.ITrackerManager
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackerManager @Inject constructor (
    private val dataStoreUseCases: DataStoreUseCases,
    private val centralizedDataSync: OrchestratorDataSyncManager,
    private val payloadProvider: PayloadProvider,
    private val sensorDataUseCases: SensorDataUseCases
): ITrackerManager {
    private var trackingJob: Job? = null

    override fun start(scope: CoroutineScope) {
        trackingJob?.cancel()
        trackingJob = scope.launch {
            try {
                // 1. Iniciar sesión si no existe
                val hasActiveSession = dataStoreUseCases.getDataString("current_session_id").isNotEmpty()
                if (!hasActiveSession) {
                    val userInfo = payloadProvider.getUserInfo()
                    val initialLocation = payloadProvider.getCurrentLocation()
                    centralizedDataSync.startMonitoringSession(userInfo, initialLocation)
                }

                // 2. Loop principal de monitoreo
                while (dataStoreUseCases.getDataBoolean(KEY_IS_MONITORING_TOGGLE)) {
                    try {

                        val currentLocation = payloadProvider.getCurrentLocation()

                        // get room data
                        val recentSensorData = sensorDataUseCases.getRecentSensorData(
                            sinceTimestamp = System.currentTimeMillis() - 30_000 // 30s
                        )

                        if (recentSensorData.isNotEmpty()) {
                            val sensorMap = recentSensorData.associateBy { it.id.toString() }

                            // El CentralizedDataSyncManager decide si enviar o no
                            centralizedDataSync.processSensorData(sensorMap, currentLocation)
                        }

                    } catch (e: Exception) {
                        Log.e("TrackerManager", "Error en ciclo de monitoreo: ${e.message}")
                    }

                    delay(10_000) //10s
                }

            } catch (e: Exception) {
                Log.e("TrackerManager", "Error en TrackerManager: ${e.message}")
            }
        }
    }

    override fun stop() {
        trackingJob?.cancel()
        trackingJob = null
    }
}