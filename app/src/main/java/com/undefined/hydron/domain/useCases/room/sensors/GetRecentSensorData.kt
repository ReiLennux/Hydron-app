package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class GetRecentSensorData(
    private val repository: ISensorDataRepository
) {
    suspend operator fun invoke(sinceTimestamp: Long): List<SensorData> =
        repository.getRecentSensorData(sinceTimestamp)
}





