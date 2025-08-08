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
        Log.d("DATA_RECIVED", "Mensaje recibido: $messageEvent")

        if (messageEvent.path == "/sensor_data") {
            val payload = String(messageEvent.data, Charsets.UTF_8)
            Log.d("DATA_RECIVED", "Payload recibido: $payload")

            val parts = payload.split(":")
            if (parts.size == 2) {
                val typeString = parts[0]
                val value = parts[1].toDoubleOrNull()

                val sensorType = try {
                    SensorType.valueOf(typeString)
                } catch (e: Exception) {
                    Log.e("WearService", "Tipo de sensor inválido: $typeString")
                    null
                }

                if (sensorType != null && value != null) {
                    val data = SensorData(
                        sensorType = sensorType,
                        value = value,
                        takenAt = System.currentTimeMillis()
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        sensorDataUseCases.addSensorData(data)
                    }
                } else {
                    Log.e("WearService", "Datos inválidos: tipo=$typeString, valor=${parts[1]}")
                }
            } else {
                Log.e("WearService", "Payload malformado: $payload")
            }
        } else {
            Log.d("WearService", "Ruta no reconocida: ${messageEvent.path}")
        }
    }

}
