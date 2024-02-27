package com.example.chatapp.model

data class MessageNotificationPayload(
    val userId: Int,
    val userName: String,
    val profilePictureUrl: String,
    val message: String,
    val action: String,
)