package com.undefined.hydron.domain.useCases.room.sensors


data class SensorDataUseCases(
    val addSensorData: AddSensorData,
    val getSensorDataByType: GetSensorDataByType,
    val deleteSensorData: DeleteSensorData,

    //db
    val getRecentSensorData: GetRecentSensorData,
    val getPendingSensorData: GetPendingSensorData,
    val markAsSent: MarkAsSent,
    val getSensorDataInTimeRange: GetSensorDataInTimeRange,
    val getActiveSensorTypes: GetActiveSensorTypes,

    //dbBatch
    val getTotalCount: GetTotalCount,
    val getRecordsBatch: GetRecordsBatch,
    val resetRecords: ResetRecords,
    val markAsUploaded: MarkAsUploaded
)
