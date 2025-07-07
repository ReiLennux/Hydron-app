package com.undefined.hydron.domain.repository

import com.undefined.hydron.domain.interfaces.IWeatherApi
import com.undefined.hydron.domain.repository.interfaces.IWeather
import javax.inject.Inject

class WeatherImpl
@Inject constructor(
    private val api: IWeatherApi
) : IWeather {
    override suspend fun getCurrentWeather(apiKey: String, city: String) = api.getCurrentWeather(apiKey, city)
}