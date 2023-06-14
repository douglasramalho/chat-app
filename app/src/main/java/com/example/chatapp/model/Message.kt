package com.example.chatapp.model

data class Message(
    val id: String,
    val conversationId: String,
    val text: String,
    val formattedTime: String,
    val isUnread: Boolean,
    val isOwnMessage: Boolean,
)
