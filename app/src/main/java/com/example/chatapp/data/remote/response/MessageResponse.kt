package com.example.chatapp.data.remote.response

import com.example.chatapp.model.Message
import com.squareup.moshi.JsonClass
import java.text.DateFormat
import java.util.Date

@JsonClass(generateAdapter = true)
data class MessageResponse(
    val id: String,
    val senderId: String,
    val receiverId: String,
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
    isOwnMessage = this.senderId == userId
)
