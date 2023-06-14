package com.example.chatapp.model

data class Conversation(
    val id: String,
    val members: List<ConversationMember>,
    val unreadCount: Int,
    val lastMessage: String?,
    val timestamp: String,
)

data class ConversationMember(
    val id: String,
    val isSelf: Boolean,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?,
)

fun Conversation.getReceiverMember() = members.first { !it.isSelf }
