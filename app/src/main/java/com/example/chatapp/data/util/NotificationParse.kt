package com.example.chatapp.data.util

import com.example.chatapp.model.MessageNotificationPayload
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class MessageNotificationPayloadDto(
    val userId: Int,
    val userName: String,
    val profileImageUrl: String,
    val message: String,
    val action: String
)

class NotificationParse @Inject constructor() {

    fun parseMessagePayload(json: String?): MessageNotificationPayload? {
        return json?.let {
            val messageNotificationPayloadDto = Json.decodeFromString<MessageNotificationPayloadDto>(it)
            MessageNotificationPayload(
                userId = messageNotificationPayloadDto.userId,
                userName = messageNotificationPayloadDto.userName,
                profilePictureUrl = messageNotificationPayloadDto.profileImageUrl,
                message = messageNotificationPayloadDto.message,
                action = messageNotificationPayloadDto.action
            )
        }
    }
}