package com.undefined.hydron.infrastructure.api

import com.undefined.hydron.domain.interfaces.IWeatherApi
import com.undefined.hydron.domain.interfaces.IWeatherApiService
import com.undefined.hydron.domain.models.entities.WeatherModel
import javax.inject.Inject

class WeatherApiServiceImpl @Inject constructor(
    private val service: IWeatherApiService
) : IWeatherApi {

    override suspend fun getCurrentWeather(location: String): WeatherModel {
        return service.getCurrentWeather(location = location)
    }
}