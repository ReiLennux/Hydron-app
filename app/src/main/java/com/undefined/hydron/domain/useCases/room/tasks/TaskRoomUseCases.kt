package com.undefined.hydron.domain.useCases.room.tasks


data class TaskRoomUseCases(
    //Example TODO: delete this use cases
    val addTask: AddTask,
    val deleteTask: DeleteTask,
    val getTasks: GetTasks,
    val updateTask: UpdateTask
)