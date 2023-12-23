package com.example.chatapp.data.network.response

import com.example.chatapp.model.Message
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

fun MessageResponse.toModel(userId: String) = Message(
    id = this.id,
    senderId = this.senderId,
    receiverId = this.receiverId,
    text = this.text,
    formattedTime = this.getFormattedTime(),
    isUnread = this.isUnread,
    isOwnMessage = this.senderId == userId.toInt()
)

private fun MessageResponse.getFormattedTime(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.timestamp
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return simpleDateFormat.format(calendar.time)
}
