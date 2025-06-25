package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.entities.Task
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {

    suspend fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Int): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}