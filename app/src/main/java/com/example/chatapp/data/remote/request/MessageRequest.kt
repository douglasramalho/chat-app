package com.example.chatapp.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val receiverId: String,
    val text: String,
)
