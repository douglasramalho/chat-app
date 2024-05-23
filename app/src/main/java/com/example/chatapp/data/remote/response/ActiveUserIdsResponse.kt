package com.example.chatapp.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ActiveUserIdsResponse(
    val activeUserIds: List<Int>
)
