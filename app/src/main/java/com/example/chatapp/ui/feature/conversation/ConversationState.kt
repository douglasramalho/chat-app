package com.example.chatapp.ui.feature.conversation

import com.example.chatapp.model.Conversation
import com.example.chatapp.model.Message
import com.example.chatapp.model.User

data class ConversationState(
    val conversation: Conversation? = null,
    val receiver: User? = null,
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)