package com.undefined.hydron.domain.repository

import android.location.Location
import com.undefined.hydron.domain.interfaces.IWeatherApi
import com.undefined.hydron.domain.models.entities.WeatherModel
import com.undefined.hydron.domain.repository.interfaces.IWeather
import javax.inject.Inject


class WeatherImpl @Inject constructor(
    private val weatherApi: IWeatherApi
) : IWeather {
    override suspend fun getCurrentWeather(query: Location): WeatherModel {
        return weatherApi.getCurrentWeather(query)
    }
}