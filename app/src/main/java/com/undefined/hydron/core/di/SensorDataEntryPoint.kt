package com.undefined.hydron.core.di

import com.undefined.hydron.domain.managers.TrackingController
import com.undefined.hydron.domain.useCases.bindData.BindDataUseCases
import com.undefined.hydron.domain.useCases.room.sensors.SensorDataUseCases
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SensorDataEntryPoint {
    fun sensorDataUseCases(): SensorDataUseCases
    fun bindDataUseCases(): BindDataUseCases
    fun trackingController(): TrackingController
}
