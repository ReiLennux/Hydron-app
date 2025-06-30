package com.undefined.hydron.dummy

import com.undefined.hydron.domain.useCases.wear.HandleWearMessage
import kotlinx.coroutines.delay

suspend fun simulateIncomingWearMessages(handler: HandleWearMessage) {
    repeat(100) { index ->
        val randomHeartRate = (60..140).random()
        delay(10_000L)
        val path = "/heart_rate"
        handler(path, randomHeartRate)
    }
}
