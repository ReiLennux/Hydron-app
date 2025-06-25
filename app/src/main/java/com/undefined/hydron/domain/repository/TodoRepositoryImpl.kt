package com.undefined.hydron.domain.repository

import com.undefined.hydron.domain.interfaces.dao.ITaskDao
import com.undefined.hydron.domain.models.entities.Task
import com.undefined.hydron.domain.repository.interfaces.ITodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepositoryImpl
@Inject constructor(
    private val dao: ITaskDao
) : ITodoRepository {

    override suspend fun getAllTasks(): Flow<List<Task>>  = dao.getAllTasks()

    override suspend fun getTaskById(id: Int): Task? = dao.getTaskById(id)

    override suspend fun insertTask(task: Task) = dao.insertTask(task)

    override suspend fun updateTask(task: Task) = dao.updateTask(task)

    override suspend fun deleteTask(task: Task) = dao.deleteTask(task)
}