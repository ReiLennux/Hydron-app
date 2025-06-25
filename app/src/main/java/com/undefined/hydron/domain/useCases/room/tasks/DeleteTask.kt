package com.undefined.hydron.domain.useCases.room.tasks

import com.undefined.hydron.domain.models.entities.Task
import com.undefined.hydron.domain.repository.interfaces.ITodoRepository

class DeleteTask(private val repo: ITodoRepository) {
    suspend operator fun invoke(task: Task) = repo.deleteTask(task)
}
