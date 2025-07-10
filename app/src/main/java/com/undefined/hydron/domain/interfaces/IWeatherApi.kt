package com.undefined.hydron.domain.interfaces

import com.undefined.hydron.domain.models.entities.WeatherModel

interface IWeatherApi {
    suspend fun getCurrentWeather(city: String): WeatherModel
}