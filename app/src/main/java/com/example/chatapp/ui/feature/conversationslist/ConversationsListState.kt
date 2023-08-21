package com.example.chatapp.ui.feature.conversationslist

import com.example.chatapp.model.Conversation

data class ConversationsListState(
    val isLoading: Boolean = false,
    val conversationsList: List<Conversation> = emptyList(),
)
