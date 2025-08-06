package com.undefined.hydron.domain.models

import com.undefined.hydron.domain.models.entities.SensorData
import com.undefined.hydron.domain.models.entities.toFirebaseMap

data class TrackingPayload(
    val location: Location? = null,
    val sensorData: Map<String, SensorData>? = null,
    val userInfo: UserInfo? = null,
    val riskLevel: Double? = null,
)



data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)

data class UserInfo(
    var userId: String = "",
    var userName: String = "",
    var gender: SexType = SexType.PrefieroNoDecirlo,
    var isActive: Boolean = false,
    var riskLevel: Double = 0.0,
    var age: Int = 0,
)


fun TrackingPayload.toFirebaseMap(): Map<String, Any> = buildMap {
    location?.let {
        put("location", mapOf(
            "latitude" to it.latitude,
            "longitude" to it.longitude,
            "timestamp" to System.currentTimeMillis()
        ))
    }

    sensorData?.takeIf { it.isNotEmpty() }?.let { sensors ->
        val mapped = sensors.mapValues { it.value.toFirebaseMap() }
        put("sensorData", mapped)
    }

    userInfo?.let {
        put("userInfo", it.toFirebaseMap())
    }
}

fun UserInfo.toFirebaseMap(): Map<String, Any?> = mapOf(
    "userId" to userId,
    "name" to userName,
    "sex" to gender.name,
    "age" to age,
    "isActive" to isActive,
    "riskLevel" to riskLevel,
)

