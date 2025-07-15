package com.undefined.hydron.infrastructure.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.undefined.hydron.core.Constants.DATABASE_NAME
import com.undefined.hydron.domain.interfaces.dao.*
import com.undefined.hydron.domain.models.entities.*
import com.undefined.hydron.infrastructure.db.preloaded.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Task::class,
        SensorData::class,
        FrecuenciaCardiaca::class,
        PresionArterial::class,
        ActividadFisica::class
    ],
    version = 15,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {

    abstract fun taskDao(): ITaskDao
    abstract fun sensorDataDao(): ISensorDataDao
    abstract fun frecuenciaCardiacaDao(): IFrecuenciaCardiacaDao
    abstract fun presionArterialDao(): IPresionArterialDao
    abstract fun actividadFisicaDao(): IActividadFisicaDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        Log.d("MainDatabase", "Beginning preload...")

                        val frecuenciaDao = instance.frecuenciaCardiacaDao()
                        val presionDao = instance.presionArterialDao()
                        val actividadDao = instance.actividadFisicaDao()

                        val count = frecuenciaDao.getCount()

                        if (count == 0) {
                            frecuenciaDao.insertAll(FrecuenciaCardiacaDataSet)
                            presionDao.insertAll(PresionArterialDataSet)
                            actividadDao.insertAll(ActividadFisicaDataSet)

                            Log.d("MainDatabase", "Preload Already")
                        } else {
                            Log.d("MainDatabase", "Skipped Preload")
                        }
                    } catch (e: Exception) {
                        Log.e("MainDatabase", "Preload Error", e)
                    }
                }

                instance
            }
        }
    }
}
