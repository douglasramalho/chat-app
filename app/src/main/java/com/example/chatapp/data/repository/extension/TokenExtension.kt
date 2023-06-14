package com.example.chatapp.data.repository.extension

import com.auth0.android.jwt.JWT

fun String?.getUserIdFromToken(): String {
    return this?.let { token ->
        val jwt = JWT(token)
        jwt.claims["userId"]?.asString() ?: ""
    } ?: ""
}