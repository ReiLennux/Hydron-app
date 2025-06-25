package com.undefined.hydron.domain.useCases.auth

import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.repository.interfaces.IAuthRepository
import com.undefined.hydron.domain.models.RegisterUser

class RegisterUser(private val repository: IAuthRepository) {
    suspend operator fun invoke(user: RegisterUser): Response<Boolean> {
        return repository.registerUser(user)
    }
}
