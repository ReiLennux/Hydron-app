package com.undefined.hydron.presentation.features.auth.register

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
import com.undefined.hydron.domain.models.LoginModel
import com.undefined.hydron.domain.models.RegisterUser
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.SexType
import com.undefined.hydron.domain.models.UserModel
import com.undefined.hydron.domain.useCases.auth.AuthUseCases
import com.undefined.hydron.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.hydron.presentation.shared.Validations
import com.undefined.hydron.presentation.shared.components.toast.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val _validations: Validations,
    private val _authUSeCases: AuthUseCases,
    private val _dataStoreUseCases: DataStoreUseCases
) : ViewModel() {

    // region Flow
    private val _isLoading = MutableStateFlow<Response<Boolean>?>(value = null)
    val isLoading: MutableStateFlow<Response<Boolean>?> = _isLoading
    //endregion

    //region Form Values
    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _repeatedPassword = MutableLiveData("")
    val repeatedPassword: LiveData<String> = _repeatedPassword

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _sex = MutableLiveData(SexType.OTHER)
    val sex: LiveData<SexType> = _sex

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val _birthDate = MutableLiveData(LocalDate.now().format(formatter))
    val birthDate: LiveData<String> = _birthDate

    private val _height = MutableLiveData(0.0)
    val height: LiveData<Double> = _height

    private val _weight = MutableLiveData(0.0)
    val weight: LiveData<Double> = _weight

    private val _hasHypertension = MutableLiveData(false)
    val hasHypertension: LiveData<Boolean> = _hasHypertension

    private val _hasDiabetes = MutableLiveData(false)
    val hasDiabetes: LiveData<Boolean> = _hasDiabetes

    private val _hasHeartDisease = MutableLiveData(false)
    val hasHeartDisease: LiveData<Boolean> = _hasHeartDisease

    private val _cDDetails = MutableLiveData("")
    val cDDetails: LiveData<String?> = _cDDetails
    //endregion

    // region Errors messages
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _repeatedPasswordError = MutableLiveData<String?>()
    val repeatedPasswordError: LiveData<String?> = _repeatedPasswordError

    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> = _nameError

    private val _sexError = MutableLiveData<String?>()
    val sexError: LiveData<String?> = _sexError

    private val _birthDateError = MutableLiveData<String?>()
    val birthDateError: LiveData<String?> = _birthDateError

    private val _heightError = MutableLiveData<String?>()
    val heightError: LiveData<String?> = _heightError

    private val _weightError = MutableLiveData<String?>()
    val weightError: LiveData<String?> = _weightError

    private val _cDDetailsError = MutableLiveData<String?>()
    val cDDetailsError: LiveData<String?> = _cDDetailsError

    //endregion

    fun resetState() {
        _isLoading.value = null
    }

    //region register
    fun onEvent(event: RegisterFormEvent) {
        when (event) {
            is RegisterFormEvent.EmailChanged -> {
                _email.value = event.email
            }

            is RegisterFormEvent.PasswordChanged -> {
                _password.value = event.password
            }

            is RegisterFormEvent.RepeatedPasswordChanged -> {
                _repeatedPassword.value = event.repeatedPassword
            }

            is RegisterFormEvent.NameChanged -> {
                _name.value = event.name
            }

            is RegisterFormEvent.SexChanged -> {
                _sex.value = event.sex
            }

            is RegisterFormEvent.BirthDateChanged -> {
                _birthDate.value = event.birthDate
            }

            is RegisterFormEvent.HeightChanged -> {
                _height.value = event.height
            }

            is RegisterFormEvent.WeightChanged -> {
                _weight.value = event.weight
            }

            is RegisterFormEvent.HasHypertensionChanged -> {
                _hasHypertension.value = event.hasKidneyDisease
            }

            is RegisterFormEvent.HasDiabetesChanged -> {
                _hasDiabetes.value = event.hasDiabetes
            }

            is RegisterFormEvent.HasHeartDiseaseChanged -> {
                _hasHeartDisease.value = event.hasCancer
            }

            is RegisterFormEvent.ChronicDiseaseDetailsChanged -> {
                _cDDetails.value = event.cDDetails
            }

            is RegisterFormEvent.Submit -> {
                submitData()
            }
        }
    }

    
    fun submitData() {
        val emailResult = _validations.validateEmail(_email.value!!)
        val passwordResult = _validations.validateStrongPassword(_password.value!!)
        val repeatedPasswordResult =
            _validations.validateRepeatedPassword(_password.value!!, _repeatedPassword.value!!)
        val nameResult = _validations.validateBasicText(_name.value!!)
        val sexResult = _validations.validateSex(_sex.value!!)
        val birthDateResult = _validations.validateBirthdate(LocalDate.parse(_birthDate.value!!, formatter))
        val heightResult = _validations.validateHeight(_height.value!!)
        val weightResult = _validations.validateWeight(_weight.value!!)

        val hasError = listOf(
            emailResult,
            passwordResult,
            repeatedPasswordResult,
            nameResult,
            sexResult,
            birthDateResult,
            heightResult,
            weightResult,
        ).any { !it.successful }

        if (hasError) {
            _emailError.value = emailResult.errorMessage
            _passwordError.value = passwordResult.errorMessage
            _repeatedPasswordError.value = repeatedPasswordResult.errorMessage
            _nameError.value = nameResult.errorMessage
            _sexError.value = sexResult.errorMessage
            _birthDateError.value = birthDateResult.errorMessage
            _heightError.value = heightResult.errorMessage
            _weightError.value = weightResult.errorMessage
            return
        }

        var user = RegisterUser(
            user = UserModel(
                name = _name.value!!,
                sex = _sex.value!!,
                birthDate = _birthDate.value!!,
                height = _height.value!!,
                weight = _weight.value!!,
                hasHypertension = _hasHypertension.value!!,
                hasDiabetes = _hasDiabetes.value!!,
                hasHeartDisease = _hasHeartDisease.value!!,
                chronicDiseaseDetails = _cDDetails.value!!
                ),
            login = LoginModel (
                email = _email.value!!,
                password = _password.value!!
            )

        )

        viewModelScope.launch {
            registerUser(user = user)
        }
    }

    private suspend fun registerUser(user: RegisterUser) = viewModelScope.async {
        _isLoading.value = Response.Loading
        val response = _authUSeCases.registerUser(user)
        if (response is Response.Success) {
            _isLoading.value = Response.Success(true)
            setDataStoreInfo(user.user)
        } else if (response is Response.Error) {
            _isLoading.value = Response.Error(response.exception)
            ToastManager.showToast(isSuccess = false, message = response.exception?.message!!)
        }
    }.await()

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

    //endregion

}