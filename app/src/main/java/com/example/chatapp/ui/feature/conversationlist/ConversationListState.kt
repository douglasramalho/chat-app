package com.example.chatapp.ui.feature.conversationlist

import com.example.chatapp.model.Conversation

data class ConversationListState(
    val isLoading: Boolean = false,
    val conversationsList: List<Conversation> = emptyList(),
)
