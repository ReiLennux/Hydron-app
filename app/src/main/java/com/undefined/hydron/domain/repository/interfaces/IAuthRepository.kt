package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.RegisterUser
import com.undefined.hydron.domain.models.Response

interface IAuthRepository {
    suspend fun registerUser(user: RegisterUser): Response<Boolean>
}