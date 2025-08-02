package com.undefined.hydron.domain.interfaces

import android.location.Location
import com.undefined.hydron.domain.models.entities.WeatherModel

interface IWeatherApi {
    suspend fun getCurrentWeather(location: Location): WeatherModel
}