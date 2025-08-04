package com.undefined.hydron.domain.repository

import com.undefined.hydron.domain.interfaces.dao.ISensorDataDao
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SensorDataRepositoryImpl @Inject constructor(
    private val sensorDataDao: ISensorDataDao
) : ISensorDataRepository {

    // Métodos existentes
    override suspend fun addSensorData(sensorData: SensorData) =
        sensorDataDao.insert(sensorData)

    override suspend fun getSensorDataByType(sensorType: SensorType): Flow<List<SensorData>> =
        sensorDataDao.getSensorDataByTypeFlow(sensorType.name)

    override suspend fun deleteSensorData(sensorData: SensorData) =
        sensorDataDao.delete(sensorData)

    // Batch existentes
    override suspend fun getTotalCount(): Int = sensorDataDao.getTotalCount()

    override suspend fun getRecordsBatch(offset: Int, limit: Int): List<SensorData> =
        sensorDataDao.getRecordsBatch(offset, limit)

    override suspend fun resetRecords() = sensorDataDao.resetRecords()

    override suspend fun markAsUploaded(ids: List<Int>) = sensorDataDao.markAsUploaded(ids)

    // NUEVOS métodos
    override suspend fun getRecentSensorData(sinceTimestamp: Long): List<SensorData> =
        sensorDataDao.getRecentSensorData(sinceTimestamp)

    override suspend fun getPendingSensorData(): List<SensorData> =
        sensorDataDao.getPendingSensorData()

    override suspend fun markAsSent(ids: List<Long>) =
        sensorDataDao.markAsSent(ids)

    override suspend fun getSensorDataInTimeRange(startTime: Long, endTime: Long): List<SensorData> =
        sensorDataDao.getSensorDataInTimeRange(startTime, endTime)

    override suspend fun getActiveSensorTypes(sinceTimestamp: Long): List<String> =
        sensorDataDao.getActiveSensorTypes(sinceTimestamp)
}