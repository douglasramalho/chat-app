package com.example.chatapp.data.network

sealed class NetworkError : Exception() {
    data object NotFound : NetworkError()
    data object BadRequest : NetworkError()
    data object Conflict : NetworkError()
    data class Generic(val code: Int, override val message: String) : NetworkError()
}