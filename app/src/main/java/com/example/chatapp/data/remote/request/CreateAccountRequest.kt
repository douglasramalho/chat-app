package com.example.chatapp.data.remote.request

data class CreateAccountRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?,
)
