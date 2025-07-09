package com.undefined.hydron.domain.models

sealed class TransferResult {
    object Success : TransferResult()
    data class Progress(val percentage: Int) : TransferResult()
    data class Error(val exception: Exception) : TransferResult()
    object Cancelled : TransferResult()
}