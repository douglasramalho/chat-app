package com.example.chatapp.model

data class Message(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    val formattedTime: String,
    val isUnread: Boolean,
    val isOwnMessage: Boolean,
)
