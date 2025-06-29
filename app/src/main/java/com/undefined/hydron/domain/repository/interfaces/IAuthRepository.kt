package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.RegisterUser
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.UserModel

interface IAuthRepository {
    suspend fun registerUser(user: RegisterUser): Response<UserModel>
    suspend fun getUser(userId: String): Response<UserModel>

}