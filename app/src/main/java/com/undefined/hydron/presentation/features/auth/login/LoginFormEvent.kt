package com.undefined.hydron.presentation.features.auth.login


sealed class LoginFormEvent {

    data class EmailChanged(val email: String): LoginFormEvent()
    data class PasswordChanged(val password: String): LoginFormEvent()

    //Action
    data object Submit: LoginFormEvent()

}