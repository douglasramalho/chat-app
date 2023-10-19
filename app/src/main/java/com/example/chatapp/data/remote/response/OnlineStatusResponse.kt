package com.example.chatapp.data.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OnlineStatusResponse(
    val onlineUserIds: List<Int>
)
