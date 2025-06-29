package com.undefined.hydron.core.di

import android.content.Context
import androidx.room.Room
import com.undefined.hydron.infrastructure.db.TodoDatabase
import com.undefined.hydron.domain.interfaces.dao.ITaskDao
import com.undefined.hydron.domain.repository.AuthRepositoryImpl
import com.undefined.hydron.domain.repository.DataStoreRepositoryImpl
import com.undefined.hydron.domain.repository.interfaces.ITodoRepository
import com.undefined.hydron.domain.repository.TodoRepositoryImpl
import com.undefined.hydron.domain.repository.interfaces.IAuthRepository
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
import com.undefined.hydron.domain.useCases.room.tasks.AddTask
import com.undefined.hydron.domain.useCases.room.tasks.DeleteTask
import com.undefined.hydron.domain.useCases.room.tasks.GetTasks
import com.undefined.hydron.domain.useCases.room.tasks.RoomUseCases
import com.undefined.hydron.domain.useCases.room.tasks.UpdateTask
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
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase =
        Room.databaseBuilder(context, TodoDatabase::class.java, "todo_db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideTaskDao(db: TodoDatabase): ITaskDao = db.taskDao()

    @Provides
    fun provideTodoRepository(dao: ITaskDao): ITodoRepository = TodoRepositoryImpl(dao)

    @Provides
    fun provideRoomUseCases(repository: ITodoRepository): RoomUseCases {
        return RoomUseCases(
            addTask = AddTask(repository),
            deleteTask = DeleteTask(repository),
            getTasks = GetTasks(repository),
            updateTask = UpdateTask(repository)
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

}

