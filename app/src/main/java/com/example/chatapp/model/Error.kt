package com.example.chatapp.model

sealed class AppError : Throwable() {
    sealed class ApiError : AppError() {
        data object Unauthorized : AppError()
        data object Conflict : AppError()
        data class Unknown(override val message: String? = null) : AppError()
    }

    sealed class DomainError : AppError() {
        data class Unknown(override val message: String? = null) : AppError()
    }
}
