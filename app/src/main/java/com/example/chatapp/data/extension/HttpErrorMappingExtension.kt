package com.example.chatapp.data.extension

import com.example.chatapp.data.remote.MyHttpException
import com.example.chatapp.model.AppError

fun Throwable.errorMapping() = when (this) {
    is MyHttpException -> when (this.code) {
        401 -> AppError.ApiError.Unauthorized
        409 -> AppError.ApiError.Conflict
        else -> AppError.ApiError.Unknown(this.message)
    }

    else -> AppError.DomainError.Unknown(this.message)
}