package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository
import kotlinx.coroutines.flow.Flow


class GetSensorDataByType(private val repository: ISensorDataRepository) {
    suspend operator fun invoke(sensorType: SensorType): Flow<List<SensorData>> {
        return repository.getSensorDataByType(sensorType)
    }
}
