package com.undefined.hydron.infrastructure.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.undefined.hydron.domain.interfaces.dao.ITaskDao
import com.undefined.hydron.domain.models.entities.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun taskDao(): ITaskDao
}
