package com.undefined.hydron.presentation.features.auth.register

import com.undefined.hydron.domain.models.SexType

sealed class RegisterFormEvent {

    //auth
    data class EmailChanged(val email: String): RegisterFormEvent()
    data class PasswordChanged(val password: String): RegisterFormEvent()
    data class RepeatedPasswordChanged(val repeatedPassword: String): RegisterFormEvent()

    //personal info
    data class NameChanged(val name: String): RegisterFormEvent()
    data class SexChanged(val sex: SexType): RegisterFormEvent()
    data class BirthDateChanged(val birthDate: String): RegisterFormEvent()
    data class HeightChanged(val height: String): RegisterFormEvent()
    data class WeightChanged(val weight: String): RegisterFormEvent()

    //more about chronic diseases
    data class HasHypertensionChanged(val hasKidneyDisease: Boolean): RegisterFormEvent()
    data class HasDiabetesChanged(val hasDiabetes: Boolean): RegisterFormEvent()
    data class HasHeartDiseaseChanged(val hasCancer: Boolean): RegisterFormEvent()

    //details for chronic diseases
    data class ChronicDiseaseDetailsChanged(val cDDetails: String?): RegisterFormEvent()

    //Action
    data object Submit: RegisterFormEvent()
}