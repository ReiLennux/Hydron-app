package com.undefined.hydron.domain.useCases.room.heartRate

import com.undefined.hydron.domain.models.entities.sensors.HeartRate
import com.undefined.hydron.domain.repository.interfaces.IHeartRateRepository

class DeleteHeartRate (private val repository: IHeartRateRepository) {
    suspend operator fun invoke(heartRate: HeartRate) {
        repository.deleteHeartRate(heartRate)
    }
}