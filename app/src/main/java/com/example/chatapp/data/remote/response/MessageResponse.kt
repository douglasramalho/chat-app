package com.example.chatapp.data.remote.response

import com.example.chatapp.model.Message
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.Date

@Serializable
data class PaginatedMessageResponse(
    val messages: List<MessageResponse>,
    val total: Int,
    val hasMore: Boolean,
)

@Serializable
data class MessageResponse(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    val timestamp: Long,
    val isUnread: Boolean,
)

fun MessageResponse.toMessage(userId: String) = Message(
    id = this.id,
    senderId = this.senderId,
    receiverId = this.receiverId,
    text = this.text,
    formattedTime = DateFormat.getDateInstance(DateFormat.DEFAULT).format(Date(this.timestamp)),
    isUnread = this.isUnread,
    isOwnMessage = this.senderId == userId.toInt()
)
