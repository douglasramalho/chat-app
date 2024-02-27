package com.example.chatapp.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPushRequest(
    val token: String,
)
