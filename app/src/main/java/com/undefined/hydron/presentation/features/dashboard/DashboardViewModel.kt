package com.undefined.hydron.presentation.features.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sensorDataUseCases: SensorDataUseCases
): ViewModel(){

    private val _showAddTaskDrawe = MutableLiveData(false)
    val showAddTaskDrawe: LiveData<Boolean> = _showAddTaskDrawe

    private val _taskTitle = MutableLiveData("")
    val taskTitle: LiveData<String> = _taskTitle

    private val _taskDescription = MutableLiveData("")
    val taskDescription: LiveData<String> = _taskDescription

    private val _registers = MutableLiveData<List<SensorData>>()
    val registers: LiveData<List<SensorData>> = _registers


    init {
        getTasks()
    }

    fun toggleAddTaskDrawe(){
        _showAddTaskDrawe.value = !_showAddTaskDrawe.value!!
    }


    fun getTasks(){
        viewModelScope.launch {
            sensorDataUseCases.getSensorDataByType(SensorType.HEART_RATE).collect {
                println(it)
                _registers.value = it
            }
        }
    }
}