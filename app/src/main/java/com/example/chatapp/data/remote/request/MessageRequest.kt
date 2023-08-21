package com.example.chatapp.data.remote.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageRequest(
    val receiverId: String,
    val text: String,
)
