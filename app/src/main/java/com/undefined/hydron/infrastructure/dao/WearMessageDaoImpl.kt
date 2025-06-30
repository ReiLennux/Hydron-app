package com.undefined.hydron.infrastructure.dao

import com.undefined.hydron.domain.interfaces.dao.IWearMessageDao
import com.undefined.hydron.domain.models.entities.sensors.HeartRate
import com.undefined.hydron.domain.useCases.room.heartRate.HeartrateRoomUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WearMessageDaoImpl @Inject constructor(
    private val _room: HeartrateRoomUseCases
) : IWearMessageDao {

    override suspend fun handleMessage(path: String, payload: Int) = withContext(Dispatchers.IO) {
        println("Mensaje recibido: $path")
        when (path) {
            "/heart_rate" -> {
                    println("BPM: $payload")
                    _room.addHeartRate(HeartRate(bpm = payload))
            }
            else -> {
                println("Ruta no reconocida: $path")
            }
        }
    }
}
