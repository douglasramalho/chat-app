package com.example.chatapp.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val receiverId: String,
    val text: String,
)
