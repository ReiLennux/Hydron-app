package com.undefined.hydron.domain.useCases.room.heartRate

import com.undefined.hydron.domain.models.entities.sensors.HeartRate
import com.undefined.hydron.domain.repository.interfaces.IHeartRateRepository
import kotlinx.coroutines.flow.Flow

class GetHeartRates (private val repository: IHeartRateRepository) {
    suspend operator fun invoke(): Flow<List<HeartRate>> {
        return repository.getHeartRates()
    }
}