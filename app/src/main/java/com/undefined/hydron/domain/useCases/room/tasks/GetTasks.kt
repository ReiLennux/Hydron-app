package com.undefined.hydron.domain.useCases.room.tasks

import com.undefined.hydron.domain.models.entities.Task
import com.undefined.hydron.domain.repository.interfaces.ITodoRepository
import kotlinx.coroutines.flow.Flow

class GetTasks(private val repo: ITodoRepository) {
    suspend operator fun invoke(): Flow<List<Task>> = repo.getAllTasks()
}
