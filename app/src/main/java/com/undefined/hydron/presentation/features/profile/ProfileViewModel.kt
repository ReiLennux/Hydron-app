package com.undefined.hydron.presentation.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.undefined.hydron.domain.models.SexType
import com.undefined.hydron.domain.models.UserModel
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.presentation.shared.components.toast.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val _dataStore: DataStoreUseCases
): ViewModel() {

    // region Flow
    private val _isLoading = MutableStateFlow<Response<Boolean>?>(null)
    val isLoading: MutableStateFlow<Response<Boolean>?> = _isLoading
    // endregion

    // region Form Values
    private val _user = MutableLiveData(UserModel())
    val user: LiveData<UserModel> = _user
    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email
    // endregion

    init {
        viewModelScope.launch {
            getUserData()
        }
    }

    private suspend fun getUserData() {
        _isLoading.value = Response.Loading

        try {
            val name = _dataStore.getDataString(USER_NAME)
            val email = _dataStore.getDataString(USER_EMAIL)
            val uid = _dataStore.getDataString(USER_UID)
            val sexString = _dataStore.getDataString(USER_SEX)
            val sex = try {
                SexType.valueOf(sexString)
            } catch (e: IllegalArgumentException) {
                SexType.PrefieroNoDecirlo
            }

            val birthDate = _dataStore.getDataString(USER_BIRTHDATE)
            val height = _dataStore.getDouble(USER_HEIGHT)
            val weight = _dataStore.getDouble(USER_WEIGHT)
            val hasHypertension = _dataStore.getDataBoolean(USER_HYPERTENSION)
            val hasDiabetes = _dataStore.getDataBoolean(USER_DIABETES)
            val hasHeartDisease = _dataStore.getDataBoolean(USER_HEART_DISEASE)
            val cDDetails = _dataStore.getDataString(USER_CHRONIC_DISEASE_DETAILS)


            _user.postValue(
                UserModel(
                    name = name,
                    uid = uid,
                    sex = sex,
                    birthDate = birthDate,
                    height = height,
                    weight = weight,
                    hasHypertension = hasHypertension,
                    hasDiabetes = hasDiabetes,
                    hasHeartDisease = hasHeartDisease,
                    chronicDiseaseDetails = cDDetails
                    )
            )
            _email.postValue(email)

            _isLoading.value = Response.Success(true)
        } catch (e: Exception) {
            _isLoading.value = Response.Error(e)
            ToastManager.showToast(isSuccess = false, message = e.message.toString())

        }

    }

    fun resetState() {
        _isLoading.value = null
    }

    fun getAge(birthDateString: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(birthDateString, formatter)
            val currentDate = LocalDate.now()
            val age = Period.between(birthDate, currentDate).years
            "$age"
        } catch (e: DateTimeParseException) {
            "00"
        }
    }

    fun logOut(){
        viewModelScope.launch {
            //_dataStore.clearDataStore()
        }
    }
}
