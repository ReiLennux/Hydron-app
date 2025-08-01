package com.undefined.hydron.presentation.shared.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.UserModel
import com.undefined.hydron.domain.useCases.auth.AuthUseCases
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class LoginCheckViewModel @Inject constructor(
    private val _fireAuth: FirebaseAuth,
    private val _dataStoreUseCases: DataStoreUseCases,
    private val _auth: AuthUseCases,
): ViewModel() {

    private val _currentUser = MutableLiveData<UserModel?>()
    val currentUser: MutableLiveData<UserModel?> = _currentUser

    private val _userResponse = MutableStateFlow<Response<UserModel>?>(value = null)
    val userResponse: StateFlow<Response<UserModel>?> = _userResponse

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin

    private val _isAuthenticationComplete = MutableStateFlow(false)
    val isAuthenticationComplete: StateFlow<Boolean> = _isAuthenticationComplete

    // Init
    init {
        getCurrentUser()
    }

    private fun getCurrentUser() = viewModelScope.launch {
        _userResponse.value = Response.Loading
        val firebaseUser = _fireAuth.currentUser

        if (firebaseUser != null) {
            val response = _auth.getUser.invoke(userId = firebaseUser.uid)
            _userResponse.value = response

            if (response is Response.Success) {
                setDataStoreInfo(response.data, firebaseUser.email ?: "")
                _isAuthenticationComplete.value = true
            } else {
                _navigateToLogin.value = true
                _isAuthenticationComplete.value = true
            }
        } else {
            _userResponse.value = Response.Error(exception = Exception("User not found"))
            _navigateToLogin.value = true
            _isAuthenticationComplete.value = true
        }
    }

    private fun setDataStoreInfo(user: UserModel, email: String) = viewModelScope.launch {
        _dataStoreUseCases.setDataString.invoke(USER_UID, user.uid!!)
        _dataStoreUseCases.setDataString.invoke(USER_NAME, user.name)
        _dataStoreUseCases.setDataInt.invoke(USER_AGE, getAge(user.birthDate))
        _dataStoreUseCases.setDataString.invoke(USER_EMAIL, email)
        _dataStoreUseCases.setDataString.invoke(USER_SEX, user.sex.toString())
        _dataStoreUseCases.setDataString.invoke(USER_BIRTHDATE, user.birthDate)
        _dataStoreUseCases.setDouble.invoke(USER_HEIGHT, user.height)
        _dataStoreUseCases.setDouble.invoke(USER_WEIGHT, user.weight)
        _dataStoreUseCases.setDataBoolean.invoke(USER_HYPERTENSION, user.hasHypertension)
        _dataStoreUseCases.setDataBoolean.invoke(USER_DIABETES, user.hasDiabetes)
        _dataStoreUseCases.setDataBoolean.invoke(USER_HEART_DISEASE, user.hasHeartDisease)
        _dataStoreUseCases.setDataString.invoke(USER_CHRONIC_DISEASE_DETAILS, user.chronicDiseaseDetails ?: "")
    }

    fun resetInitialState() {
        _userResponse.value = null
        _navigateToLogin.value = false
        _isAuthenticationComplete.value = false
    }

    fun assignCurrentUser(currentUser: UserModel) {
        _currentUser.value = currentUser
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

//    fun handleLogout() {
//        _currentUser.value = null
//        _userResponse.value = null
//        _navigateToLogin.value = true
//        _isAuthenticationComplete.value = true
//    }
}