package com.undefined.hydron.domain.interfaces

interface IDataStore {

    suspend fun setDataString(key: String, value: String)

    suspend fun getDataString(key: String): String

    suspend fun setDataBoolean(key: String, value: Boolean)

    suspend fun getDataBoolean(key: String): Boolean

    suspend fun setDataInt(key: String, value: Int)

    suspend fun getDataInt(key: String): Int

    suspend fun setDouble(key: String, value: Double)

    suspend fun getDouble(key: String): Double


}