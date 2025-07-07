package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.entities.WeatherModel


interface IWeather {
    suspend fun getCurrentWeather(query: String): WeatherModel
}