package com.example.chatapp.data.extension

import com.example.chatapp.model.AppError
import retrofit2.HttpException

fun Throwable.errorMapping() = when (this) {
    is HttpException -> when (this.code()) {
        401 -> AppError.ApiError.Unauthorized
        409 -> AppError.ApiError.Conflict
        else -> AppError.ApiError.Unknown(this.message())
    }

    else -> AppError.DomainError.Unknown(this.message)
}