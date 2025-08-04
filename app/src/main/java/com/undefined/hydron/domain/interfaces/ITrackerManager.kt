package com.undefined.hydron.domain.interfaces

import kotlinx.coroutines.CoroutineScope

interface ITrackerManager {

    fun start(scope: CoroutineScope)

    fun stop()
}