package com.undefined.hydron.domain.useCases.dataStore

import com.undefined.hydron.domain.repository.DataStoreRepositoryImpl
import javax.inject.Inject

class SetDataBoolean @Inject constructor(
    private val _repository: DataStoreRepositoryImpl
) {
    suspend operator fun invoke(key: String, value: Boolean) = _repository.setDataBoolean(key, value)
}