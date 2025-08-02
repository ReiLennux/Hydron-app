package com.undefined.hydron.domain.repository.interfaces

import android.location.Location
import com.undefined.hydron.domain.models.entities.WeatherModel


interface IWeather {
    suspend fun getCurrentWeather(query: Location): WeatherModel
}