package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class GetSensorDataInTimeRange(
    private val repository: ISensorDataRepository
) {
    suspend operator fun invoke(startTime: Long, endTime: Long): List<SensorData> =
        repository.getSensorDataInTimeRange(startTime, endTime)
}