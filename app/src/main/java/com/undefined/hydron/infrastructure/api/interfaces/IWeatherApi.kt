package com.undefined.hydron.infrastructure.api.interfaces

import com.undefined.hydron.BuildConfig
import com.undefined.hydron.domain.models.entities.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherApi {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") query: String,
        @Query("key") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): WeatherModel
}