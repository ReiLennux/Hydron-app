package com.undefined.hydron.domain.useCases.auth

data class AuthUseCases (
    val registerUser: RegisterUser,
    val getUser: GetUser,
    val loginUser: LoginUser,
    //TODO: Add logoutUser
    //val logoutUser: LogoutUser

)