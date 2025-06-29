package com.undefined.hydron.domain.useCases.auth

import com.undefined.hydron.domain.repository.interfaces.IAuthRepository

class GetUser(private val repository: IAuthRepository) {
    suspend operator fun invoke(userId: String) = repository.getUser(userId)

}