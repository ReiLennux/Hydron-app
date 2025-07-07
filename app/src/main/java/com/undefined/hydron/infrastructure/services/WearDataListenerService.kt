package com.undefined.hydron.infrastructure.services

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dagger.hilt.android.EntryPointAccessors
import com.undefined.hydron.core.di.SensorDataEntryPoint

class WearDataListenerService : WearableListenerService() {

    private lateinit var sensorDataUseCases: SensorDataUseCases

    override fun onCreate() {
        super.onCreate()
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            SensorDataEntryPoint::class.java
        )
        sensorDataUseCases = entryPoint.sensorDataUseCases()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/sensor_data") {
            val payload = String(messageEvent.data, Charsets.UTF_8)
            Log.d("WearService", "Mensaje recibido: $payload")

            val value = payload.toFloatOrNull()
            if (value != null) {
                val data = SensorData(
                    sensorType = SensorType.HEART_RATE, // Usa uno fijo si solo envías uno
                    value = value,
                    takenAt = System.currentTimeMillis()
                )

                CoroutineScope(Dispatchers.IO).launch {
                    sensorDataUseCases.addSensorData(data)
                }
            } else {
                Log.e("WearService", "Payload inválido: $payload")
            }
        } else {
            Log.d("WearService", "Ruta no reconocida: ${messageEvent.path}")
        }
    }
}
