package com.example.chatapp.data.extension

import com.example.chatapp.data.network.NetworkError
import com.example.chatapp.model.AppError

fun Throwable.errorMapping() = when (this) {
    is NetworkError -> when (this) {
        is NetworkError.Unauthorized -> AppError.ApiError.Unauthorized
        is NetworkError.NotFound -> AppError.ApiError.NotFound
        is NetworkError.BadRequest -> AppError.ApiError.BadRequest
        is NetworkError.Conflict -> AppError.ApiError.Conflict
        else -> AppError.ApiError.Unknown(this.message)
    }

    else -> AppError.DomainError.Unknown(this.message)
}