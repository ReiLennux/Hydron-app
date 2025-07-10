package com.undefined.hydron.domain.interfaces.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.undefined.hydron.domain.models.entities.SensorData
import kotlinx.coroutines.flow.Flow

@Dao
interface ISensorDataDao {


    @Query("SELECT * FROM sensor_data WHERE sensorType = :type AND value != 0 ORDER BY takenAt ASC")
    fun  getSensorDataByTypeFlow(type: String): Flow<List<SensorData>>

    @Insert
    suspend fun insert(sensorData: SensorData)

    @Delete
    suspend fun delete(sensorData: SensorData)

    //db Batch

    @Query("SELECT COUNT(*) FROM sensor_data")
    suspend fun getTotalCount(): Int

    @Query("SELECT * FROM sensor_data ORDER BY takenAt ASC LIMIT :limit OFFSET :offset")
    suspend fun getRecordsBatch(offset: Int, limit: Int): List<SensorData>

    @Query("DELETE FROM sensor_data WHERE isUploaded = 1")
    suspend fun resetRecords()

    @Query("UPDATE sensor_data SET isUploaded = 1 WHERE id IN (:ids)")
    suspend fun markAsUploaded(ids: List<Int>)

}
