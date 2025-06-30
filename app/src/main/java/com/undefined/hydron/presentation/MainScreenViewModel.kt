package com.undefined.hydron.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
import com.undefined.hydron.infrastructure.receivers.PhoneMessageReceiver
import com.undefined.hydron.presentation.shared.navigation.enums.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val _fireAuth: FirebaseAuth,
    private val _dataStoreUseCases: DataStoreUseCases,
    private val _auth: AuthUseCases,
    val receiver: PhoneMessageReceiver

): ViewModel() {

    //Variables
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _currentUser = MutableLiveData<UserModel?>()
    val currentUser: MutableLiveData<UserModel?> = _currentUser

    private val _userResponse = MutableStateFlow<Response<UserModel>?>(value = null)
    val userResponse: MutableStateFlow<Response<UserModel>?> = _userResponse

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin




    // Init
    init {
        getCurrentUser()
        receiver.simulateMessage()
    }



    private fun getCurrentUser() = viewModelScope.launch {
        _userResponse.value = Response.Loading
        val firebaseUser = _fireAuth.currentUser
        if (firebaseUser != null) {
            val response = _auth.getUser.invoke(userId = firebaseUser.uid)
            _userResponse.value = response
            if (response is Response.Success) {
                setDataStoreInfo(response.data)
            }
        } else {
            _userResponse.value = Response.Error(exception = Exception("User not found"))
            _navigateToLogin.value = true
        }
    }


    private fun setDataStoreInfo(user: UserModel) = viewModelScope.launch {
        _dataStoreUseCases.setDataString.invoke(USER_UID, user.uid!!)
        _dataStoreUseCases.setDataString.invoke(USER_NAME, user.name)
        _dataStoreUseCases.setDataString.invoke(USER_EMAIL, user.name)
        _dataStoreUseCases.setDataString.invoke(USER_SEX, user.sex.toString())
        _dataStoreUseCases.setDataString.invoke(USER_BIRTHDATE, user.birthDate)
        _dataStoreUseCases.setDouble.invoke(USER_HEIGHT, user.height)
        _dataStoreUseCases.setDouble.invoke(USER_WEIGHT, user.weight)
        _dataStoreUseCases.setDataBoolean.invoke(USER_HYPERTENSION, user.hasHypertension)
        _dataStoreUseCases.setDataBoolean.invoke(USER_DIABETES, user.hasDiabetes)
        _dataStoreUseCases.setDataBoolean.invoke(USER_HEART_DISEASE, user.hasHeartDisease)
        _dataStoreUseCases.setDataString.invoke(USER_CHRONIC_DISEASE_DETAILS, user.chronicDiseaseDetails?: "")
    }

    fun resetInitialState(){
        _userResponse.value = null
    }

    fun assignCurrentUser(currentUser: UserModel){
        _currentUser.value = currentUser
    }


    fun verifyRouteTop(currentRoute: String?): Boolean {
        return !(currentRoute == Routes.LOGIN.name || currentRoute == Routes.SIGN_UP.name)
    }

    fun verifyRouteBottom(currentRoute: String?): Boolean {
        return !(currentRoute == Routes.LOGIN.name || currentRoute == Routes.SIGN_UP.name)
    }
}