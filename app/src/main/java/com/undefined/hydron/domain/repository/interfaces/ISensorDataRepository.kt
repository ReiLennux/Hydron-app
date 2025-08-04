package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import kotlinx.coroutines.flow.Flow

interface ISensorDataRepository {
    // Métodos existentes
    suspend fun addSensorData(sensorData: SensorData)
    suspend fun getSensorDataByType(sensorType: SensorType): Flow<List<SensorData>>
    suspend fun deleteSensorData(sensorData: SensorData)

    // Batch existentes
    suspend fun getTotalCount(): Int
    suspend fun getRecordsBatch(offset: Int, limit: Int): List<SensorData>
    suspend fun resetRecords()
    suspend fun markAsUploaded(ids: List<Int>)

    // NUEVOS para sistema centralizado
    suspend fun getRecentSensorData(sinceTimestamp: Long): List<SensorData>
    suspend fun getPendingSensorData(): List<SensorData>
    suspend fun markAsSent(ids: List<Long>)
    suspend fun getSensorDataInTimeRange(startTime: Long, endTime: Long): List<SensorData>
    suspend fun getActiveSensorTypes(sinceTimestamp: Long): List<String>
}