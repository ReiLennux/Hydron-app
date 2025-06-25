package com.undefined.hydron.domain.useCases.room.tasks

data class RoomUseCases(
    val addTask: AddTask,
    val deleteTask: DeleteTask,
    val getTasks: GetTasks,
    val updateTask: UpdateTask
)
