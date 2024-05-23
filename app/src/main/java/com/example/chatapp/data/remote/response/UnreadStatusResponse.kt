package com.example.chatapp.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class UnreadStatusResponse(
    val hasConversationsUnread: Boolean,
    val unreadMessagesCount: Int,
)
