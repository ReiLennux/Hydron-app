package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class MarkAsSent(
    private val repository: ISensorDataRepository
) {
    suspend operator fun invoke(ids: List<Long>) =
        repository.markAsSent(ids)
}