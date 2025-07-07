package com.undefined.hydron.core.di

import android.content.Context
import androidx.room.Room
import com.undefined.hydron.domain.interfaces.IWeatherApi
import com.undefined.hydron.domain.interfaces.dao.ISensorDataDao
import com.undefined.hydron.domain.interfaces.dao.ITaskDao
import com.undefined.hydron.domain.interfaces.dao.IWearMessageDao
import com.undefined.hydron.domain.repository.AuthRepositoryImpl
import com.undefined.hydron.domain.repository.DataStoreRepositoryImpl
import com.undefined.hydron.domain.repository.SensorDataRepositoryImpl
import com.undefined.hydron.domain.repository.interfaces.ITodoRepository
import com.undefined.hydron.domain.repository.TodoRepositoryImpl
import com.undefined.hydron.domain.repository.WeatherImpl
import com.undefined.hydron.domain.repository.interfaces.IAuthRepository
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository
import com.undefined.hydron.domain.repository.interfaces.IWeather
import com.undefined.hydron.domain.useCases.auth.AuthUseCases
import com.undefined.hydron.domain.useCases.auth.GetUser
import com.undefined.hydron.domain.useCases.auth.LoginUser
import com.undefined.hydron.domain.useCases.auth.RegisterUser
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.domain.useCases.dataStore.GetDataBoolean
import com.undefined.hydron.domain.useCases.dataStore.GetDataInt
import com.undefined.hydron.domain.useCases.dataStore.GetDataString
import com.undefined.hydron.domain.useCases.dataStore.GetDouble
import com.undefined.hydron.domain.useCases.dataStore.SetDataBoolean
import com.undefined.hydron.domain.useCases.dataStore.SetDataInt
import com.undefined.hydron.domain.useCases.dataStore.SetDataString
import com.undefined.hydron.domain.useCases.dataStore.SetDouble
import com.undefined.hydron.domain.useCases.room.sensors.AddSensorData
import com.undefined.hydron.domain.useCases.room.sensors.DeleteSensorData
import com.undefined.hydron.domain.useCases.room.sensors.GetSensorDataByType
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import com.undefined.hydron.domain.useCases.room.tasks.AddTask
import com.undefined.hydron.domain.useCases.room.tasks.DeleteTask
import com.undefined.hydron.domain.useCases.room.tasks.GetTasks
import com.undefined.hydron.domain.useCases.room.tasks.TaskRoomUseCases
import com.undefined.hydron.domain.useCases.room.tasks.UpdateTask
import com.undefined.hydron.domain.useCases.wear.HandleWearMessage
import com.undefined.hydron.domain.useCases.weather.GetCurrentWeather
import com.undefined.hydron.domain.useCases.weather.WeatherUseCases
import com.undefined.hydron.infrastructure.dao.WearMessageDaoImpl
import com.undefined.hydron.infrastructure.db.MainDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDataStoreUseCases(dataStoreRepository: DataStoreRepositoryImpl): DataStoreUseCases =
        DataStoreUseCases(
            setDataString = SetDataString(dataStoreRepository),
            getDataString = GetDataString(dataStoreRepository),
            setDataBoolean = SetDataBoolean(dataStoreRepository),
            getDataBoolean = GetDataBoolean(dataStoreRepository),
            setDataInt = SetDataInt(dataStoreRepository),
            getDataInt = GetDataInt(dataStoreRepository),
            setDouble = SetDouble(dataStoreRepository),
            getDouble = GetDouble(dataStoreRepository)
        )

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MainDatabase =
        Room.databaseBuilder(context, MainDatabase::class.java, "Hydron_db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideTaskDao(db: MainDatabase): ITaskDao = db.taskDao()

    @Provides
    fun provideTodoRepository(dao: ITaskDao): ITodoRepository = TodoRepositoryImpl(dao)

    @Provides
    fun provideRoomUseCases(repository: ITodoRepository): TaskRoomUseCases {
        return TaskRoomUseCases(
            addTask = AddTask(repository),
            deleteTask = DeleteTask(repository),
            getTasks = GetTasks(repository),
            updateTask = UpdateTask(repository)
        )
    }

    @Provides
    fun provideHeartRateDao(db: MainDatabase): ISensorDataDao = db.sensorDataDao()

    @Provides
    fun provideHeartRateRepository(dao: ISensorDataDao): ISensorDataRepository =
        SensorDataRepositoryImpl(dao)

    @Provides
    fun provideHeartRateUseCases(repository: ISensorDataRepository): SensorDataUseCases {
        return SensorDataUseCases(
            addSensorData = AddSensorData(repository),
            deleteSensorData = DeleteSensorData(repository),
            getSensorDataByType = GetSensorDataByType(repository)
        )
    }

    @Singleton
    @Provides
    fun provideAuthRepository(): IAuthRepository = AuthRepositoryImpl()

    @Provides
    fun provideAuthUseCases(repository: IAuthRepository): AuthUseCases =
            AuthUseCases(
                registerUser = RegisterUser(repository),
                getUser = GetUser(repository),
                loginUser = LoginUser(repository)
            )

    @Provides
    fun provideWearMessageDao(room: SensorDataUseCases): IWearMessageDao {
        return WearMessageDaoImpl(room)
    }


    @Provides
    fun provideHandleWearMessage(dao: IWearMessageDao): HandleWearMessage = HandleWearMessage(dao)

    @Provides
    fun provideWeatherRepository(api: IWeatherApi): IWeather = WeatherImpl(api)


    @Provides
    fun provideWeatherUseCases(
        getCurrentWeather: GetCurrentWeather
    ): WeatherUseCases {
        return WeatherUseCases(getCurrentWeather)
    }



    @Provides
    fun provideGetCurrentWeatherUseCase(
        repository: IWeather
    ): GetCurrentWeather {
        return GetCurrentWeather(repository)
    }

}

