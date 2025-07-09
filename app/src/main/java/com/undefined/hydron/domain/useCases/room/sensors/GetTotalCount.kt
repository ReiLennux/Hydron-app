package com.undefined.hydron.domain.useCases.room.sensors

import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository

class GetTotalCount(private val repository: ISensorDataRepository) {
    suspend operator fun invoke(): Int {
        return repository.getTotalCount()

    }

}