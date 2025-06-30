package com.undefined.hydron.infrastructure.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.undefined.hydron.domain.interfaces.dao.IHeartRateDao
import com.undefined.hydron.domain.interfaces.dao.ITaskDao
import com.undefined.hydron.domain.models.entities.Task
import com.undefined.hydron.domain.models.entities.sensors.HeartRate

@Database(entities = [Task::class, HeartRate::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun taskDao(): ITaskDao
    abstract fun heartRateDao(): IHeartRateDao
}
