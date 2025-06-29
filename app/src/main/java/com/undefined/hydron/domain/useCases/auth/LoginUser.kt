package com.undefined.hydron.domain.useCases.auth

import com.undefined.hydron.domain.models.LoginModel
import com.undefined.hydron.domain.repository.interfaces.IAuthRepository

class LoginUser(private val repository: IAuthRepository) {
    suspend operator fun invoke(loginUser: LoginModel) = repository.loginUser(loginUser)

}
