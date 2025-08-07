package com.undefined.hydron.presentation.features.home

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.TransferResult
import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.SensorType
import com.undefined.hydron.domain.models.entities.WeatherModel
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import com.undefined.hydron.domain.useCases.weather.WeatherUseCases
import com.undefined.hydron.infrastructure.services.DataTransferService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sensorDataUseCases: SensorDataUseCases,
    private val weatherUseCases: WeatherUseCases,
    private val dataTransferService: DataTransferService,
    application: Application,
    @ApplicationContext private val context: Context
) : AndroidViewModel(application) {


    // region Flow
    private val _isLoading = MutableStateFlow<Response<WeatherModel>?>(null)
    val isLoading: MutableStateFlow<Response<WeatherModel>?> = _isLoading
    // endregion

    private val _hearthRateRegisters = MutableLiveData<List<SensorData>>()
    val hearthRateRegisters: LiveData<List<SensorData>> = _hearthRateRegisters

    private val _temperatureRegisters = MutableLiveData<List<SensorData>>()
    val temperatureRegisters: LiveData<List<SensorData>> = _temperatureRegisters

    private val _stepCountRegisters = MutableLiveData<List<SensorData>>()
    val stepCountRegisters: LiveData<List<SensorData>> = _stepCountRegisters


    private val _weather = MutableLiveData<WeatherModel?>()
    val weather: LiveData<WeatherModel?> = _weather

    //region Batch temporal
    private val _transferState = MutableLiveData<TransferUiState>()
    val transferState: LiveData<TransferUiState> = _transferState

    private val _totalRecords = MutableLiveData<Int>()
    val totalRecords: LiveData<Int> = _totalRecords
    //endregion

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null
    private val minDistanceMeters = 3f // 3mts


    init {
        fetchLocationOnce()
        getSensorData()
        getTotalRecords()
        viewModelScope.launch {
            simulateData()
        }
    }

    private suspend fun simulateData() {
        var steps = 100.0

        while (true) {
            val stepIncrement = (2..22).random()
            steps += stepIncrement

            val randomHeartRate = (85..98).random()
            val randomTemp = (34..37).random()

            viewModelScope.launch {
                sensorDataUseCases.addSensorData(
                    SensorData(
                        sensorType = SensorType.HEART_RATE,
                        value = randomHeartRate.toDouble(),
                    )
                )

                sensorDataUseCases.addSensorData(
                    SensorData(
                        sensorType = SensorType.TEMPERATURE,
                        value = randomTemp.toDouble(),
                    )
                )

                sensorDataUseCases.addSensorData(
                    SensorData(
                        sensorType = SensorType.STEP_COUNT,
                        value = steps
                    )
                )
            }

            delay(5_000L)  //5s
        }
    }

    sealed class TransferUiState {
        object Idle : TransferUiState()
        data class Transferring(val progress: Int) : TransferUiState()
        object Success : TransferUiState()
        data class Error(val message: String) : TransferUiState()
    }

    fun getTotalRecords() {
        viewModelScope.launch {
            val count = sensorDataUseCases.getTotalCount()
            _totalRecords.value = count
        }
    }

    fun startDataTransfer() {
        _transferState.postValue( TransferUiState.Transferring(0))

        dataTransferService.startDataTransfer { result ->
            when (result) {
                is TransferResult.Progress -> {
                    _transferState.postValue( TransferUiState.Transferring(result.percentage))
                }
                is TransferResult.Success -> {
                    _transferState.postValue(TransferUiState.Success)
                    getTotalRecords()
                }
                is TransferResult.Error -> {
                    _transferState.postValue( TransferUiState.Error(result.exception.message ?: "Error desconocido"))
                }
                is TransferResult.Cancelled -> {
                    _transferState.postValue(TransferUiState.Idle)
                }
            }
        }
    }

    fun cancelDataTransfer() {
        dataTransferService.cancelTransfer()
        _transferState.postValue( TransferUiState.Idle)
    }

    fun resetTransferState() {
        _transferState.postValue( TransferUiState.Idle)
    }
    //endregion

    //region Location

    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000L // 5s
            fastestInterval = 2000L
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val newLocation = locationResult.lastLocation
                if (newLocation != null && shouldUpdate(newLocation)) {
                    _location.postValue(newLocation)
                    lastLocation = newLocation
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                context.mainLooper
            )
        } catch (e: SecurityException) {
            Log.e("HomeViewModel", "Permiso de ubicación no otorgado", e)
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    private fun shouldUpdate(newLoc: Location): Boolean {
        val last = lastLocation ?: return true
        return last.distanceTo(newLoc) >= minDistanceMeters
    }

    fun fetchLocationOnce() {
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.lastLocation
                location.addOnSuccessListener {
                    it?.let { loc ->
                        _location.postValue(loc)
                        viewModelScope.launch {
                            fetchWeather(loc) // <-- pasa la ubicación directamente
                        }
                    } ?: run {
                        println("Ubicación nula")
                    }
                }

            } catch (_: SecurityException) {
                _location.postValue(null)
            }
        }
    }
    //endregion

    //region weather

    fun fetchWeather(location: Location) {
        viewModelScope.launch {
            _isLoading.value = Response.Loading
            try {
                val weather = weatherUseCases.getCurrentWeather(location)
                _isLoading.value = Response.Success(weather)
            } catch (e: Exception) {
                _isLoading.value = Response.Error(e)
            }
        }
    }


    fun resetState() {
        _isLoading.value = null
    }

    fun setWeather(weather: WeatherModel) {
        _weather.value = weather
    }

    //endregion

    //region Sensor

    fun getSensorData(){
        viewModelScope.launch {
            sensorDataUseCases.getSensorDataByType(SensorType.HEART_RATE).collect {
                _hearthRateRegisters.value = it
            }

        }
        viewModelScope.launch {
            sensorDataUseCases.getSensorDataByType(SensorType.TEMPERATURE).collect {
                _temperatureRegisters.value = it
            }
        }
        viewModelScope.launch {
            sensorDataUseCases.getSensorDataByType(SensorType.STEP_COUNT).collect {
                _stepCountRegisters.value = it
            }
        }

    }

    //endregion


}

