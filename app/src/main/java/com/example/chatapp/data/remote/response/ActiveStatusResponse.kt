package com.example.chatapp.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ActiveStatusResponse(
    val activeUserIds: List<Int>
)
