package com.undefined.hydron.domain.models

import java.time.LocalDate

data class RegisterUser(
    val user: UserModel,
    val login: LoginModel
)

data class UserModel(
    val uid: String? = null,
    val name: String = "",
    val sex: SexType = SexType.OTHER,
    val birthDate: String = LocalDate.now().toString(),
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val hasHypertension: Boolean = false,
    val hasDiabetes: Boolean = false,
    val hasHeartDisease: Boolean = false,
    val chronicDiseaseDetails: String? = null

)

data class LoginModel(
    val email: String = "",
    val password: String = ""
)

enum class SexType {
    MALE,
    FEMALE,
    OTHER

}