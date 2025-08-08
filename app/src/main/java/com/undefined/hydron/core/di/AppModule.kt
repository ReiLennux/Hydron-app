package com.undefined.hydron.core.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.undefined.hydron.domain.interfaces.ILocationProvider
import com.undefined.hydron.domain.interfaces.ITrackerManager
import com.undefined.hydron.domain.interfaces.ITrackingController
import com.undefined.hydron.domain.interfaces.dao.IWearMessageDao
import com.undefined.hydron.domain.managers.OrchestratorDataSyncManager
import com.undefined.hydron.domain.managers.PayloadProvider
import com.undefined.hydron.domain.managers.RiskAnalyzer
import com.undefined.hydron.domain.managers.TrackerManager
import com.undefined.hydron.domain.managers.TrackingController
import com.undefined.hydron.domain.repository.DataStoreRepositoryImpl
import com.undefined.hydron.domain.repository.interfaces.IAuthRepository
import com.undefined.hydron.domain.repository.interfaces.IBindDataRepository
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository
import com.undefined.hydron.domain.useCases.auth.AuthUseCases
import com.undefined.hydron.domain.useCases.bindData.BindData
import com.undefined.hydron.domain.useCases.bindData.BindDataUseCases
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.domain.useCases.dataStore.GetDataBoolean
import com.undefined.hydron.domain.useCases.dataStore.GetDataBooleanFlow
import com.undefined.hydron.domain.useCases.dataStore.GetDataInt
import com.undefined.hydron.domain.useCases.dataStore.GetDataString
import com.undefined.hydron.domain.useCases.dataStore.GetDouble
import com.undefined.hydron.domain.useCases.dataStore.SetDataBoolean
import com.undefined.hydron.domain.useCases.dataStore.SetDataInt
import com.undefined.hydron.domain.useCases.dataStore.SetDataString
import com.undefined.hydron.domain.useCases.dataStore.SetDouble
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import com.undefined.hydron.domain.useCases.room.tasks.TaskRoomUseCases
import com.undefined.hydron.domain.useCases.wear.HandleWearMessage
import com.undefined.hydron.domain.useCases.weather.GetCurrentWeather
import com.undefined.hydron.domain.useCases.weather.WeatherUseCases
import com.undefined.hydron.infrastructure.db.MainDatabase
import com.undefined.hydron.infrastructure.services.DataTransferService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Database
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MainDatabase =
        Room.databaseBuilder(context, MainDatabase::class.java, "Hydron_db")
            .fallbackToDestructiveMigration(false)
            .build()

    // DataStore Use Cases
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
            getDouble = GetDouble(dataStoreRepository),
            getDataBooleanFlow = GetDataBooleanFlow(dataStoreRepository)
        )

    // Task Use Cases
    @Provides
    fun provideRoomUseCases(repository: com.undefined.hydron.domain.repository.interfaces.ITodoRepository): TaskRoomUseCases {
        return TaskRoomUseCases(
            addTask = com.undefined.hydron.domain.useCases.room.tasks.AddTask(repository),
            deleteTask = com.undefined.hydron.domain.useCases.room.tasks.DeleteTask(repository),
            getTasks = com.undefined.hydron.domain.useCases.room.tasks.GetTasks(repository),
            updateTask = com.undefined.hydron.domain.useCases.room.tasks.UpdateTask(repository)
        )
    }

    // Sensor Data Use Cases
    @Provides
    fun provideSensorDataUseCases(repository: ISensorDataRepository): SensorDataUseCases {
        return SensorDataUseCases(
            addSensorData = com.undefined.hydron.domain.useCases.room.sensors.AddSensorData(repository),
            deleteSensorData = com.undefined.hydron.domain.useCases.room.sensors.DeleteSensorData(repository),
            getSensorDataByType = com.undefined.hydron.domain.useCases.room.sensors.GetSensorDataByType(repository),
            getTotalCount = com.undefined.hydron.domain.useCases.room.sensors.GetTotalCount(repository),
            getRecordsBatch = com.undefined.hydron.domain.useCases.room.sensors.GetRecordsBatch(repository),
            resetRecords = com.undefined.hydron.domain.useCases.room.sensors.ResetRecords(repository),
            markAsUploaded = com.undefined.hydron.domain.useCases.room.sensors.MarkAsUploaded(repository),
            getRecentSensorData = com.undefined.hydron.domain.useCases.room.sensors.GetRecentSensorData(repository),
            getPendingSensorData = com.undefined.hydron.domain.useCases.room.sensors.GetPendingSensorData(repository),
            markAsSent = com.undefined.hydron.domain.useCases.room.sensors.MarkAsSent(repository),
            getSensorDataInTimeRange = com.undefined.hydron.domain.useCases.room.sensors.GetSensorDataInTimeRange(repository),
            getActiveSensorTypes = com.undefined.hydron.domain.useCases.room.sensors.GetActiveSensorTypes(repository)
        )
    }

    // Auth Use Cases
    @Provides
    fun provideAuthUseCases(repository: IAuthRepository): AuthUseCases =
        AuthUseCases(
            registerUser = com.undefined.hydron.domain.useCases.auth.RegisterUser(repository),
            getUser = com.undefined.hydron.domain.useCases.auth.GetUser(repository),
            loginUser = com.undefined.hydron.domain.useCases.auth.LoginUser(repository)
        )



    @Provides
    fun provideHandleWearMessage(dao: IWearMessageDao): HandleWearMessage = HandleWearMessage(dao)

    // Weather Use Cases
    @Provides
    fun provideWeatherUseCases(getCurrentWeather: GetCurrentWeather): WeatherUseCases {
        return WeatherUseCases(getCurrentWeather)
    }

    @Provides
    fun provideGetCurrentWeatherUseCase(
        repository: com.undefined.hydron.domain.repository.interfaces.IWeather
    ): GetCurrentWeather {
        return GetCurrentWeather(repository)
    }

    // Firebase Database
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    // Data Transfer Service
    @Provides
    @Singleton
    fun provideDataTransferService(
        sensorDataUseCases: SensorDataUseCases,
        firebaseDatabase: FirebaseDatabase,
        dataStoreUseCases: DataStoreUseCases,
        authUseCases: AuthUseCases,
        firebaseAuth: FirebaseAuth
    ): DataTransferService {
        return DataTransferService(
            sensorDataUseCases,
            firebaseDatabase,
            firebaseAuth
            )
    }

    // Wearable Clients
    @Provides
    @Singleton
    fun provideDataClient(@ApplicationContext context: Context): DataClient {
        return Wearable.getDataClient(context)
    }

    @Provides
    @Singleton
    fun provideMessageClient(@ApplicationContext context: Context): MessageClient =
        Wearable.getMessageClient(context)

    // Location Client
    @Provides
    @Singleton
    fun provideLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // BindData Use Cases
    @Provides
    @Singleton
    fun provideBindDataUseCases(repository: IBindDataRepository): BindDataUseCases =
        BindDataUseCases(bindData = BindData(repository))

    // Risk Analyzer
    @Provides
    @Singleton
    fun provideRiskAnalyzer(): RiskAnalyzer = RiskAnalyzer()

    // Payload Provider
    @Provides
    @Singleton
    fun providePayloadProvider(
        fusedLocationClient: FusedLocationProviderClient,
        @ApplicationContext context: Context,
        auth: FirebaseAuth,
        dataStoreUseCases: DataStoreUseCases
    ): PayloadProvider = PayloadProvider(fusedLocationClient, context, auth, dataStoreUseCases)

    // Centralized Data Sync Manager
    @Provides
    @Singleton
    fun provideCentralizedDataSyncManager(
        dataStoreUseCases: DataStoreUseCases,
        riskAnalyzer: RiskAnalyzer,
        bindDataUseCases: BindDataUseCases,
        locationProvider: ILocationProvider
    ): OrchestratorDataSyncManager = OrchestratorDataSyncManager(
        dataStoreUseCases = dataStoreUseCases,
        riskAnalyzer = riskAnalyzer,
        bindDataUseCases = bindDataUseCases,
        locationProvider = locationProvider
    )

    // Tracker Manager
    @Provides
    @Singleton
    fun provideTrackerManager(
        dataStoreUseCases: DataStoreUseCases,
        centralizedDataSync: OrchestratorDataSyncManager,
        payloadProvider: PayloadProvider,
        sensorDataUseCases: SensorDataUseCases
    ): ITrackerManager = TrackerManager(
        dataStoreUseCases,
        centralizedDataSync,
        payloadProvider,
        sensorDataUseCases
    )

    // Tracking Controller
    @Provides
    @Singleton
    fun provideTrackingController(
        @ApplicationContext appContext: Context,
        dataStoreUseCases: DataStoreUseCases,
        trackerManager: ITrackerManager,
        centralizedDataSync: OrchestratorDataSyncManager,
        dataClient: DataClient
    ): ITrackingController = TrackingController(
        appContext,
        dataStoreUseCases,
        trackerManager,
        centralizedDataSync,
        dataClient

    )
}