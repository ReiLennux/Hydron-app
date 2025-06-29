package com.undefined.hydron.presentation.features.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.hydron.domain.models.LoginModel
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.useCases.auth.AuthUseCases
import com.undefined.hydron.presentation.shared.Validations
import com.undefined.hydron.presentation.shared.components.toast.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val _auth: AuthUseCases,
    private val _validations: Validations
): ViewModel(){

    // region Flow
    private val _isLoading = MutableStateFlow<Response<Boolean>?>(value = null)
    val isLoading: MutableStateFlow<Response<Boolean>?> = _isLoading
    //endregion

    //region Form Values
    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password
    //endregion

    // region Errors messages
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    //endregion

    fun resetState() {
        _isLoading.value = null
    }

    init {
        println("ProfileViewModel")
    }

    //Region OnEvent
    fun onEvent(event: LoginFormEvent) {
        when(event) {
            is LoginFormEvent.EmailChanged -> {
                _email.value = event.email
            }
            is LoginFormEvent.PasswordChanged -> {
                _password.value = event.password
            }
            is LoginFormEvent.Submit -> {
                submitData()
                }
        }
    }
    //endregion

    //region login
    fun submitData() {
        val emailResult = _validations.validateEmail(_email.value!!)
        val passwordResult = _validations.validateStrongPassword(_password.value!!)

        val hasError = listOf(
            emailResult,
            passwordResult
        ).any { !it.successful }

        if(hasError) {
            _emailError.value = emailResult.errorMessage
            _passwordError.value = passwordResult.errorMessage
            return
        }

        var login = LoginModel(
            email = _email.value!!,
            password = _password.value!!
        )

        Log.d("LOGIN", "Email: '${login.email}'")
        Log.d("LOGIN", "Password: '${login.password}'")


        viewModelScope.launch {
            loginUser(login)
        }
    }

    private suspend fun loginUser(login: LoginModel) {
        _isLoading.value = Response.Loading
        val response = _auth.loginUser(login)
        if(response is Response.Success) {
            _isLoading.value = Response.Success(true)
            } else if(response is Response.Error) {
            _isLoading.value = Response.Error(response.exception)
            ToastManager.showToast(isSuccess = false, message = response.exception?.message!!)

        }
    }
    //endregion

}