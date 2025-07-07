package com.undefined.hydron.domain.interfaces

import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherApi {
    @GET("v1/current.json")
    suspend fun  getCurrentWeather (
        @Query("key") apiKey: String,
        @Query("q") city: String
    )
}