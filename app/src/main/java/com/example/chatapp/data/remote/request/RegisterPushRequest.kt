package com.example.chatapp.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPushRequest(
    val token: String,
)
