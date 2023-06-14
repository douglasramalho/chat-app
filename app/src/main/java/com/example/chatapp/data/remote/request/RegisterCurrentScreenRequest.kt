package com.example.chatapp.data.remote.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterCurrentScreenRequest(
    val screenName: String,
    val conversationId: String?
)
