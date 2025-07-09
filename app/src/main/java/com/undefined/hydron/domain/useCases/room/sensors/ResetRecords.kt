package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class ResetRecords(private val repository: ISensorDataRepository) {
    suspend operator fun invoke() {
        return repository.resetRecords()

    }
}