package com.undefined.hydron.core.di

import android.content.Context
import com.undefined.hydron.domain.interfaces.ILocationProvider
import com.undefined.hydron.domain.interfaces.IWeatherApi
import com.undefined.hydron.domain.interfaces.dao.ISensorDataDao
import com.undefined.hydron.domain.interfaces.dao.ITaskDao
import com.undefined.hydron.domain.repository.AuthRepositoryImpl
import com.undefined.hydron.domain.repository.BindDataRepositoryImpl
import com.undefined.hydron.domain.repository.SensorDataRepositoryImpl
import com.undefined.hydron.domain.repository.TodoRepositoryImpl
import com.undefined.hydron.domain.repository.WeatherImpl
import com.undefined.hydron.domain.repository.interfaces.IAuthRepository
import com.undefined.hydron.domain.repository.interfaces.IBindDataRepository
import com.undefined.hydron.domain.repository.interfaces.ISensorDataRepository
import com.undefined.hydron.domain.repository.interfaces.ITodoRepository
import com.undefined.hydron.domain.repository.interfaces.IWeather
import com.undefined.hydron.infrastructure.db.MainDatabase
import com.undefined.hydron.infrastructure.location.LocationProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideTodoRepository(dao: ITaskDao): ITodoRepository = TodoRepositoryImpl(dao)

    @Provides
    fun provideSensorDataDao(db: MainDatabase): ISensorDataDao = db.sensorDataDao()

    @Provides
    fun provideSensorDataRepository(dao: ISensorDataDao): ISensorDataRepository =
        SensorDataRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideAuthRepository(): IAuthRepository = AuthRepositoryImpl()

    @Provides
    @Singleton
    fun provideBindDataRepository(): IBindDataRepository = BindDataRepositoryImpl()

    @Provides
    fun provideWeatherRepository(api: IWeatherApi): IWeather = WeatherImpl(api)

    @Provides
    fun provideTaskDao(db: MainDatabase): ITaskDao = db.taskDao()

    @Provides
    @Singleton
    fun provideLocationProvider(@ApplicationContext context: Context): ILocationProvider {
        return LocationProviderImpl(context)
    }


}