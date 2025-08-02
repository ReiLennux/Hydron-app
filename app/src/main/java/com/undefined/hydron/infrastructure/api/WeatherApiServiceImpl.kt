package com.undefined.hydron.infrastructure.api

import android.location.Location
import com.undefined.hydron.domain.interfaces.IWeatherApi
import com.undefined.hydron.domain.interfaces.IWeatherApiService
import com.undefined.hydron.domain.models.entities.WeatherModel
import javax.inject.Inject

class WeatherApiServiceImpl @Inject constructor(
    private val service: IWeatherApiService
) : IWeatherApi {

    override suspend fun getCurrentWeather(location: Location): WeatherModel {
        val query = "${location.latitude},${location.longitude}"
        return service.getCurrentWeather(location = query)
    }
}