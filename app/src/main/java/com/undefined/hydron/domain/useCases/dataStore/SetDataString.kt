package com.undefined.hydron.domain.useCases.dataStore

import com.undefined.hydron.domain.repository.DataStoreRepositoryImpl
import javax.inject.Inject

class SetDataString @Inject constructor(
    private val _repository: DataStoreRepositoryImpl
) {
    suspend operator fun invoke(key: String, value: String) = _repository.setDataString(key, value)
}