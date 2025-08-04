package com.undefined.hydron.domain.useCases.dataStore

import com.undefined.hydron.domain.repository.DataStoreRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDataBooleanFlow @Inject constructor(
    private val repository: DataStoreRepositoryImpl
) {
    operator fun invoke(key: String): Flow<Boolean> {
        return repository.getDataBooleanFlow(key)
    }
}
