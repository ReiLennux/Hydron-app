package com.undefined.hydron.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.hydron.core.Constants.USER_EMAIL
import com.undefined.hydron.core.Constants.USER_NAME
import com.undefined.hydron.core.Constants.USER_TYPE
import com.undefined.hydron.core.Constants.USER_UID
import com.undefined.hydron.core.Constants.USER_VERIFIED
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.UserModel
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.presentation.shared.navigation.enums.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val _dataStoreUseCases: DataStoreUseCases
): ViewModel() {

    //Variables
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _currentUser = MutableLiveData<UserModel?>()
    val currentUser: MutableLiveData<UserModel?> = _currentUser

    private val _userResponse = MutableStateFlow<Response<UserModel>?>(value = null)
    val userResponse: MutableStateFlow<Response<UserModel>?> = _userResponse


    // Init
    init {
        verifyUser()
    }

    //Functions
    private fun verifyUser() {
        getCurrentUser()
    }

    private fun getCurrentUser() = viewModelScope.launch {
        _userResponse.value = Response.Loading
        val response = Response.Success(UserModel())
        if (response is Response.Success){
            setDataStoreInfo(response.data)
        }
    }

    private fun setDataStoreInfo(user: UserModel) = viewModelScope.launch {
        _dataStoreUseCases.setDataString.invoke(USER_TYPE, "")
        _dataStoreUseCases.setDataString.invoke(USER_UID, "")
        _dataStoreUseCases.setDataString.invoke(USER_NAME, "")
        _dataStoreUseCases.setDataString.invoke(USER_EMAIL, "")
        _dataStoreUseCases.setDataBoolean.invoke(USER_VERIFIED, true)
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