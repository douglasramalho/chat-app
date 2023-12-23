package com.example.chatapp.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class UnreadStatusResponse(
    val hasConversationsUnread: Boolean,
    val unreadMessagesCount: Int,
)
