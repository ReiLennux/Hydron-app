package com.undefined.hydron.domain.useCases.dataStore

import com.undefined.hydron.domain.repository.DataStoreRepositoryImpl
import javax.inject.Inject

class GetDataBoolean @Inject constructor(
    private val _repository: DataStoreRepositoryImpl
) {
    suspend operator fun invoke(key: String): Boolean = _repository.getDataBoolean(key)
}