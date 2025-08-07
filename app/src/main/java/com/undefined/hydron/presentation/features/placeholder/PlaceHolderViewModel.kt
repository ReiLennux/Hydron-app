package com.undefined.hydron.presentation.features.placeholder

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.undefined.hydron.infrastructure.services.DataTransferService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.undefined.hydron.domain.models.TransferResult
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class PlaceHolderViewModel @Inject constructor(
    private val transferService: DataTransferService,
    private val sensorDataUseCases: SensorDataUseCases

): ViewModel(){


    init {
        getAllRecords()
    }


    private val _transferState = mutableStateOf<TransferState>(TransferState.Idle)
    val transferState: State<TransferState> = _transferState

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems



    private fun getAllRecords() {
        viewModelScope.launch {
            val count = sensorDataUseCases.getTotalCount()
            _totalItems.value = count
        }
    }


    fun startTransfer() {
        _transferState.value = TransferState.Loading(0)

        transferService.startDataTransfer { result ->
            // Garantiza que el cambio de estado ocurre en el hilo principal
            viewModelScope.launch {
                when (result) {
                    TransferResult.Success -> {
                        _transferState.value = TransferState.Success
                    }
                    is TransferResult.Progress -> {
                        _transferState.value = TransferState.Loading(result.percentage)
                    }
                    is TransferResult.Error -> {
                        _transferState.value = TransferState.Error(result.exception.message)
                    }
                    TransferResult.Cancelled -> {
                        _transferState.value = TransferState.Cancelled
                    }
                }
            }
        }

    }

    fun cancelTransfer() {
        transferService.cancelTransfer()
        _transferState.value = TransferState.Cancelled
    }

    sealed class TransferState {
        object Idle : TransferState()
        data class Loading(val progress: Int) : TransferState()
        object Success : TransferState()
        object Cancelled : TransferState()
        data class Error(val message: String?) : TransferState()
    }

}