package com.undefined.hydron.domain.models

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null,
)
