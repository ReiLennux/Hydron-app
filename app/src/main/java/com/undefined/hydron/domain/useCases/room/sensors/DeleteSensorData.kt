package com.undefined.hydron.domain.useCases.room.sensors


import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class DeleteSensorData(private val repository: ISensorDataRepository) {
    suspend operator fun invoke(sensorData: SensorData) {
        repository.deleteSensorData(sensorData)
    }
}
