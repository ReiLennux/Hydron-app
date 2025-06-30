package com.undefined.hydron.domain.useCases.room.heartRate

import com.undefined.hydron.domain.models.entities.sensors.HeartRate
import com.undefined.hydron.domain.repository.interfaces.IHeartRateRepository

class AddHeartRate (private val repository: IHeartRateRepository) {
    suspend operator fun invoke(heartRate: HeartRate) {
        repository.addHeartRate(heartRate)
    }
}