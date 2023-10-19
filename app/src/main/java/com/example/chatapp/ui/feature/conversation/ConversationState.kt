package com.example.chatapp.ui.feature.conversation

import com.example.chatapp.model.Message
import com.example.chatapp.model.User

data class ConversationState(
    val receiver: User? = null,
    val messages: List<Message> = emptyList(),
    val isOnline: Boolean = false,
    val isLoading: Boolean = false
)