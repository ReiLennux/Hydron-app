package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import kotlinx.coroutines.flow.Flow

interface ISensorDataRepository {

    suspend fun addSensorData(sensorData: SensorData)
    suspend fun getSensorDataByType(sensorType: SensorType): Flow<List<SensorData>>
    suspend fun deleteSensorData(sensorData: SensorData)
}
