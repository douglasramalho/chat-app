package com.example.chatapp.data.remote.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageRequest(
    val conversationId: String,
    val text: String,
)
