package com.undefined.hydron.domain.interfaces.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.undefined.hydron.domain.models.entities.SensorData
import kotlinx.coroutines.flow.Flow

@Dao
interface ISensorDataDao {


    @Query("SELECT * FROM sensor_data WHERE sensorType = :type ORDER BY takenAt DESC")
    fun  getSensorDataByTypeFlow(type: String): Flow<List<SensorData>>

    @Insert
    suspend fun insert(sensorData: SensorData)

    @Delete
    suspend fun delete(sensorData: SensorData)
}
