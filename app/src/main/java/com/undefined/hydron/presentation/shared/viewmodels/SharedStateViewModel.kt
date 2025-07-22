package com.undefined.hydron.presentation.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.undefined.hydron.core.Constants.KEY_IS_MONITORING_TOGGLE
import com.undefined.hydron.core.Constants.PATH_TOGGLE_MONITOR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SharedStateViewModel @Inject constructor(
    private val dataClient: DataClient
) : ViewModel(), DataClient.OnDataChangedListener {

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring

    init {
        dataClient.addListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == PATH_TOGGLE_MONITOR) {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val newValue = dataMap.getBoolean(KEY_IS_MONITORING_TOGGLE)
                    _isMonitoring.value = newValue
                }
            }
        }
    }

    fun setMonitoring(enable: Boolean) {
        viewModelScope.launch {
            val putDataMapReq = PutDataMapRequest.create(PATH_TOGGLE_MONITOR).apply {
                dataMap.putBoolean(KEY_IS_MONITORING_TOGGLE, enable)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }

            val request = putDataMapReq.asPutDataRequest().setUrgent()
            try {
                dataClient.putDataItem(request).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dataClient.removeListener(this)
    }
}
