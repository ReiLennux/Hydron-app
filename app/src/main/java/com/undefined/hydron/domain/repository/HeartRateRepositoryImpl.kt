package com.undefined.hydron.domain.repository

import com.undefined.hydron.domain.interfaces.dao.IHeartRateDao
import com.undefined.hydron.domain.models.entities.sensors.HeartRate
import com.undefined.hydron.domain.repository.interfaces.IHeartRateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HeartRateRepositoryImpl
@Inject constructor(
    private val heartRateDao: IHeartRateDao
) : IHeartRateRepository {
    override suspend fun getHeartRates(): Flow<List<HeartRate>> =
        heartRateDao.getHeartRates()

    override suspend fun addHeartRate(heartRate: HeartRate) =
        heartRateDao.addHeartRate(heartRate)

    override suspend fun deleteHeartRate(heartRate: HeartRate) =
        heartRateDao.deleteHeartRate(heartRate)

}