package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class MarkAsUploaded(private val repository: ISensorDataRepository) {
    suspend operator fun invoke(ids: List<Int>) {
        return repository.markAsUploaded(ids)
    }
}