package com.undefined.hydron.domain.repository

import com.undefined.hydron.infrastructure.services.DataStoreService
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val _dataStoreService: DataStoreService
) {
    suspend fun setDataString(key: String, value: String) {
        _dataStoreService.setDataString(key, value)
    }

    suspend fun getDataString(key: String): String {
        return _dataStoreService.getDataString(key)
    }

    suspend fun setDataBoolean(key: String, value: Boolean) {
        _dataStoreService.setDataBoolean(key, value)
    }

    suspend fun getDataBoolean(key: String): Boolean {
        return _dataStoreService.getDataBoolean(key)
    }

    suspend fun setDataInt(key: String, value: Int) {
        _dataStoreService.setDataInt(key, value)
    }

    suspend fun getDataInt(key: String): Int {
        return _dataStoreService.getDataInt(key)
    }

    suspend fun setDouble(key: String, value: Double) {
        _dataStoreService.setDouble(key, value)
    }

    suspend fun getDouble(key: String): Double {
        return _dataStoreService.getDouble(key)
    }


}