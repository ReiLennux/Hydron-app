package com.undefined.hydron.presentation.shared.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.undefined.hydron.core.Constants.KEY_IS_MONITORING_TOGGLE
import com.undefined.hydron.core.Constants.PATH_TOGGLE_MONITOR
import com.undefined.hydron.domain.interfaces.ITrackingController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedStateViewModel @Inject constructor(
    private val trackingController: ITrackingController,
    private val dataClient: DataClient
) : ViewModel(), DataClient.OnDataChangedListener {

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring

    init {
        Log.d("SharedStateViewModel", "Agregando listener a DataClient")
        dataClient.addListener(this)

        viewModelScope.launch {
            trackingController.getMonitoringFlow().collect { enabled ->
                _isMonitoring.value = enabled
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == PATH_TOGGLE_MONITOR) {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val newValue = dataMap.getBoolean(KEY_IS_MONITORING_TOGGLE)

                    Log.d("SharedStateViewModel", "onDataChanged - Recibido: $newValue")

                    if (newValue != _isMonitoring.value) {
                        _isMonitoring.value = newValue
                        // No llamamos toggleTracking para evitar bucle infinito
                    }
                }
            }
        }
    }

    fun toggleMonitoring() {
        viewModelScope.launch {
            val newValue = !_isMonitoring.value
            Log.d("SharedStateViewModel", "toggleMonitoring() -> $newValue")
            trackingController.toggleTracking(newValue, viewModelScope)
        }
    }

    override fun onCleared() {
        super.onCleared()
        dataClient.removeListener(this)
    }
}
