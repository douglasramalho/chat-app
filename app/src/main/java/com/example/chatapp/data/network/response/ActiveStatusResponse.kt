package com.example.chatapp.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class ActiveStatusResponse(
    val activeUserIds: List<Int>
)
