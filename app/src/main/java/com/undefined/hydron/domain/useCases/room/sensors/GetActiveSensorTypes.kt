package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class GetActiveSensorTypes(
    private val repository: ISensorDataRepository
) {
    suspend operator fun invoke(sinceTimestamp: Long): List<String> =
        repository.getActiveSensorTypes(sinceTimestamp)
}