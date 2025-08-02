package com.undefined.hydron.presentation.features.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import com.undefined.hydron.domain.models.entities.WeatherModel
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import com.undefined.hydron.domain.useCases.weather.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sensorDataUseCases: SensorDataUseCases,
    private val weatherUseCases: WeatherUseCases
): ViewModel(){


    // region Flow
    private val _isLoading = MutableStateFlow<Response<WeatherModel>?>(null)
    val isLoading: MutableStateFlow<Response<WeatherModel>?> = _isLoading
    // endregion


    private val _registers = MutableLiveData<List<SensorData>>()
    val registers: LiveData<List<SensorData>> = _registers

    private val _weather = MutableLiveData<WeatherModel?>()
    val weather: LiveData<WeatherModel?> = _weather


    private val _location = MutableLiveData("Tula de allende")
    val location: LiveData<String> = _location


    init {
        getTasks()
        fetchWeather()
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _isLoading.value = Response.Loading
            try {
                //val weather = weatherUseCases.getCurrentWeather(_location.value!!)
                //_isLoading.value = Response.Success(weather)
            } catch (e: Exception) {
                _isLoading.value = Response.Error(e)
            }
        }
    }



    fun getTasks(){
        viewModelScope.launch {
            sensorDataUseCases.getSensorDataByType(SensorType.HEART_RATE).collect {
                println(it)
                _registers.value = it
            }
        }
    }

    fun resetState() {
        _isLoading.value = null
    }

    fun setWeather(weather: WeatherModel) {
        _weather.value = weather
    }
}