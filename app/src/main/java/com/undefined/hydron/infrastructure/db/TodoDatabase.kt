package com.undefined.hydron.infrastructure.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.undefined.hydron.domain.interfaces.dao.ISensorDataDao
import com.undefined.hydron.domain.interfaces.dao.ITaskDao
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.Task

@Database(entities = [Task::class, SensorData::class], version = 4, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun taskDao(): ITaskDao
    abstract fun sensorDataDao(): ISensorDataDao
}
