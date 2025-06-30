package com.undefined.hydron.domain.interfaces.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.undefined.hydron.domain.models.entities.sensors.HeartRate
import kotlinx.coroutines.flow.Flow

@Dao
interface IHeartRateDao {

    @Query("SELECT * FROM heart_rates")
    fun getHeartRates(): Flow<List<HeartRate>>

    @Insert
    suspend fun addHeartRate(heartRate: HeartRate)

    @Delete
    suspend fun deleteHeartRate(heartRate: HeartRate)

}