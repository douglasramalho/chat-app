package com.example.chatapp.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val profilePictureId: String?,
)
