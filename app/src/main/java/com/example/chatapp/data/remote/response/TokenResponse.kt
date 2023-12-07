package com.example.chatapp.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String
)
