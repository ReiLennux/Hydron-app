package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class GetRecordsBatch(private val repository: ISensorDataRepository) {
    suspend operator fun invoke(offset: Int, limit: Int): List<SensorData> {
        return repository.getRecordsBatch(offset, limit)
    }
}