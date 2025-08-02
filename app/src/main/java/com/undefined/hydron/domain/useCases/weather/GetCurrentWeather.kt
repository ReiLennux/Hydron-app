package com.undefined.hydron.domain.useCases.weather

import android.location.Location
import com.undefined.hydron.domain.models.entities.WeatherModel
import com.undefined.hydron.domain.repository.interfaces.IWeather

class GetCurrentWeather(private val repository: IWeather) {
    suspend operator fun invoke(query: Location): WeatherModel {
        return repository.getCurrentWeather(query)
    }
}