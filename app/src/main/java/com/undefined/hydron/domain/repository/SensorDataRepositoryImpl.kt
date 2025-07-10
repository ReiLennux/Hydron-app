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

    override suspend fun addSensorData(sensorData: SensorData) =
        sensorDataDao.insert(sensorData)

    override suspend fun getSensorDataByType(sensorType: SensorType): Flow<List<SensorData>> =
        sensorDataDao.getSensorDataByTypeFlow(sensorType.name)


    override suspend fun deleteSensorData(sensorData: SensorData) =
        sensorDataDao.delete(sensorData)

    //dbBatch
    override suspend fun getTotalCount(): Int = sensorDataDao.getTotalCount()

    override suspend fun getRecordsBatch(offset: Int, limit: Int): List<SensorData> =
        sensorDataDao.getRecordsBatch(offset, limit)

    override suspend fun resetRecords() = sensorDataDao.resetRecords()

    override suspend fun markAsUploaded(ids: List<Int>) = sensorDataDao.markAsUploaded(ids)
}
