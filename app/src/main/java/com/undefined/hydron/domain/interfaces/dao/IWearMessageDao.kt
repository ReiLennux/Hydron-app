package com.undefined.hydron.domain.interfaces.dao


interface IWearMessageDao {
    suspend fun handleMessage(path: String, payload: Int)
}