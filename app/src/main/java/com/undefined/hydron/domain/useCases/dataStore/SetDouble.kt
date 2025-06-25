package com.undefined.hydron.domain.useCases.dataStore

import com.undefined.hydron.domain.repository.DataStoreRepositoryImpl
import javax.inject.Inject

data class SetDouble  @Inject constructor(
    private val _repository: DataStoreRepositoryImpl
) {
    suspend operator fun invoke(key: String, value: Double) = _repository.setDouble(key, value)
}