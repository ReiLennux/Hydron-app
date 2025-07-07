package com.undefined.hydron.domain.interfaces

import com.undefined.hydron.BuildConfig
import com.undefined.hydron.domain.models.entities.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query


interface IWeatherApiService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("q") location: String,
        @Query("aqi") airQuality: String = "no",
        @Query("lang") language: String = "es"
    ): WeatherModel
}