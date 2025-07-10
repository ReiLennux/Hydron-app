package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import kotlinx.coroutines.flow.Flow

interface ISensorDataRepository {

    suspend fun addSensorData(sensorData: SensorData)
    suspend fun getSensorDataByType(sensorType: SensorType): Flow<List<SensorData>>
    suspend fun deleteSensorData(sensorData: SensorData)


    //dbBatch
    suspend fun getTotalCount(): Int
    suspend fun getRecordsBatch(offset: Int, limit: Int): List<SensorData>
    suspend fun resetRecords()
    suspend fun markAsUploaded(ids: List<Int>)
}
