package com.example.chatapp.data.remote.response

import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@JsonClass(generateAdapter = true)
data class ConversationResponse(
    val id: String,
    val members: List<ConversationMemberResponse>,
    val unreadCount: Int,
    val lastMessage: String?,
    val timestamp: Long,
)

@JsonClass(generateAdapter = true)
data class ConversationMemberResponse(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?,
)

fun ConversationResponse.toConversation(userId: String): Conversation {

    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(this.timestamp)

    return Conversation(
        id = this.id,
        members = this.members.map { it.toMember(userId) },
        unreadCount = this.unreadCount,
        lastMessage = this.lastMessage,
        timestamp = sdf.format(date)
    )
}

fun ConversationMemberResponse.toMember(userId: String) = ConversationMember(
    id = this.id,
    isSelf = this.id == userId,
    username = this.username,
    firstName = this.firstName,
    lastName = this.lastName,
    profilePictureUrl = this.profilePictureUrl
)
