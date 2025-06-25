package com.undefined.hydron.presentation.features.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.hydron.domain.models.entities.Task
import com.undefined.hydron.domain.useCases.room.tasks.RoomUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val roomUseCases: RoomUseCases
): ViewModel(){

    private val _showAddTaskDrawe = MutableLiveData(false)
    val showAddTaskDrawe: LiveData<Boolean> = _showAddTaskDrawe

    private val _taskTitle = MutableLiveData("")
    val taskTitle: LiveData<String> = _taskTitle

    private val _taskDescription = MutableLiveData("")
    val taskDescription: LiveData<String> = _taskDescription

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks


    init {
        getTasks()
    }

    fun toggleAddTaskDrawe(){
        _showAddTaskDrawe.value = !_showAddTaskDrawe.value!!
    }

    fun setTaskTitle(newTitle: String) {
        _taskTitle.value = newTitle
    }

    fun setTaskDescription(newDesc: String) {
        _taskDescription.value = newDesc
    }

    fun getTasks(){
        viewModelScope.launch {
            roomUseCases.getTasks().collect {
                _tasks.value = it
            }
        }
    }

    fun addTask() {
        val newTask = Task(
            title = _taskTitle.value ?: "",
            description = _taskDescription.value ?: "",
            isCompleted = false
        )
        viewModelScope.launch {
            roomUseCases.addTask(newTask)
        }
        getTasks()

    }

    fun deleteTask(task: Task){
        viewModelScope.launch {
            roomUseCases.deleteTask(task)
        }
        getTasks()

    }

    fun updateTask(task: Task){
        viewModelScope.launch {
            roomUseCases.updateTask(task)
        }
        getTasks()
    }



}