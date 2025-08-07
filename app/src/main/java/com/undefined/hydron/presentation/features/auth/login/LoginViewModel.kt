package com.undefined.hydron.presentation.features.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.hydron.core.Constants.USER_AGE
import com.undefined.hydron.core.Constants.USER_BIRTHDATE
import com.undefined.hydron.core.Constants.USER_CHRONIC_DISEASE_DETAILS
import com.undefined.hydron.core.Constants.USER_DIABETES
import com.undefined.hydron.core.Constants.USER_EMAIL
import com.undefined.hydron.core.Constants.USER_HEART_DISEASE
import com.undefined.hydron.core.Constants.USER_HEIGHT
import com.undefined.hydron.core.Constants.USER_HYPERTENSION
import com.undefined.hydron.core.Constants.USER_NAME
import com.undefined.hydron.core.Constants.USER_SEX
import com.undefined.hydron.core.Constants.USER_UID
import com.undefined.hydron.core.Constants.USER_WEIGHT
import com.undefined.hydron.domain.models.LoginModel
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.UserModel
import com.undefined.hydron.domain.useCases.auth.AuthUseCases
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.presentation.shared.Validations
import com.undefined.hydron.presentation.shared.components.toast.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val _auth: AuthUseCases,
    private val _validations: Validations,
    private val _dataStoreUseCases: DataStoreUseCases
): ViewModel() {

    // region Flow
    private val _isLoading = MutableStateFlow<Response<Boolean>?>(value = null)
    val isLoading: MutableStateFlow<Response<Boolean>?> = _isLoading

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: MutableStateFlow<Boolean> = _loginSuccess
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
        _loginSuccess.value = false
        _emailError.value = null
        _passwordError.value = null
    }


    //Region OnEvent
    fun onEvent(event: LoginFormEvent) {
        when(event) {
            is LoginFormEvent.EmailChanged -> {
                _email.value = event.email
                _emailError.value = null
            }
            is LoginFormEvent.PasswordChanged -> {
                _password.value = event.password
                _passwordError.value = null
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
            ToastManager.showToast(isSuccess = false, message = "Por favor corrige los errores en el formulario")
            return
        }

        val login = LoginModel(
            email = _email.value!!,
            password = _password.value!!
        )

        viewModelScope.launch {
            loginUser(login)
        }
    }

    private suspend fun loginUser(login: LoginModel) {
        _isLoading.value = Response.Loading

        try {
            val response = _auth.loginUser(login)

            when(response) {
                is Response.Success -> {
                    val userDataResponse = _auth.getUser.invoke(userId = response.data.uid!!)

                    if (userDataResponse is Response.Success) {
                        setDataStoreInfo(userDataResponse.data, login.email)

                        _isLoading.value = Response.Success(true)
                        _loginSuccess.value = true
                        ToastManager.showToast(isSuccess = true, message = "Bienvenido de vuelta!")
                    } else {
                        _isLoading.value = Response.Error(Exception("Error al obtener datos del usuario"))
                        ToastManager.showToast(isSuccess = false, message = "Error al cargar datos del usuario")
                    }
                }
                is Response.Error -> {
                    _isLoading.value = Response.Error(response.exception)
                    ToastManager.showToast(isSuccess = false, message = response.exception?.message ?: "Error al iniciar sesión")
                }
                else -> {
                    _isLoading.value = Response.Error(Exception("Respuesta inesperada"))
                }
            }
        } catch (e: Exception) {
            _isLoading.value = Response.Error(e)
            ToastManager.showToast(isSuccess = false, message = "Error inesperado: ${e.message}")
        }
    }

    private fun setDataStoreInfo(user: UserModel, email: String) = viewModelScope.launch {
        _dataStoreUseCases.setDataString.invoke(USER_UID, user.uid!!)
        _dataStoreUseCases.setDataString.invoke(USER_NAME, user.name)
        _dataStoreUseCases.setDataString.invoke(USER_EMAIL, email)
        _dataStoreUseCases.setDataInt.invoke(USER_AGE, getAge(user.birthDate))
        _dataStoreUseCases.setDataString.invoke(USER_SEX, user.sex.toString())
        _dataStoreUseCases.setDataString.invoke(USER_BIRTHDATE, user.birthDate)
        _dataStoreUseCases.setDouble.invoke(USER_HEIGHT, user.height)
        _dataStoreUseCases.setDouble.invoke(USER_WEIGHT, user.weight)
        _dataStoreUseCases.setDataBoolean.invoke(USER_HYPERTENSION, user.hasHypertension)
        _dataStoreUseCases.setDataBoolean.invoke(USER_DIABETES, user.hasDiabetes)
        _dataStoreUseCases.setDataBoolean.invoke(USER_HEART_DISEASE, user.hasHeartDisease)
        _dataStoreUseCases.setDataString.invoke(USER_CHRONIC_DISEASE_DETAILS, user.chronicDiseaseDetails ?: "")
    }

    private fun getAge(dateString: String): Int {
        return try {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dateString, formatter)
            val now = LocalDate.now()

            var age = now.year - birthDate.year
            if (now.dayOfYear < birthDate.dayOfYear) {
                age--
            }
            age
        } catch (_: Exception) {
            0
        }
    }
    //endregion
}