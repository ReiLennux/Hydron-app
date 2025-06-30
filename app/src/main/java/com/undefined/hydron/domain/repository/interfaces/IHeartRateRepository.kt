package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.entities.sensors.HeartRate
import kotlinx.coroutines.flow.Flow

interface IHeartRateRepository {

    suspend fun getHeartRates(): Flow<List<HeartRate>>
    suspend fun addHeartRate(heartRate: HeartRate)
    suspend fun deleteHeartRate(heartRate: HeartRate)

}