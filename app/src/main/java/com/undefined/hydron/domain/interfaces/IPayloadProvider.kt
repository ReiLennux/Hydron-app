package com.undefined.hydron.domain.interfaces

import com.undefined.hydron.domain.models.Location
import com.undefined.hydron.domain.models.UserInfo

interface IPayloadProvider {

    suspend fun getUserInfo(): UserInfo

    suspend fun getCurrentLocation(): Location
}