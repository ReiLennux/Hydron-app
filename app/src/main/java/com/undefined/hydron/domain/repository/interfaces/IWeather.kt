package com.undefined.hydron.domain.repository.interfaces


interface IWeather {
    suspend fun getCurrentWeather(apiKey: String, city: String)
}