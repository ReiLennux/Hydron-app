package com.undefined.hydron.domain.useCases.room.sensors


data class SensorDataUseCases(
    val addSensorData: AddSensorData,
    val getSensorDataByType: GetSensorDataByType,
    val deleteSensorData: DeleteSensorData
)
