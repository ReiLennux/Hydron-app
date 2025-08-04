package com.undefined.hydron.infrastructure.dao

import com.undefined.hydron.domain.interfaces.dao.IWearMessageDao
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WearMessageDaoImpl @Inject constructor(
    private val _room: SensorDataUseCases
) : IWearMessageDao {

    override suspend fun handleMessage(path: String, payload: Int) = withContext(Dispatchers.IO) {
        println("Mensaje recibido: $path")
        when (path) {
            "/sensor_data" -> {
                    println("BPM: $payload")
                    _room.addSensorData(SensorData(
                        sensorType = SensorType.HEART_RATE,
                        value = payload.toDouble()
                    ))
            }
            else -> {
                println("Ruta no reconocida: $path")
            }
        }
    }
}
