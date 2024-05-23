package com.example.chatapp.data.remote.ws

import com.example.chatapp.data.WebSocketDataSerializer

@kotlinx.serialization.Serializable(with = WebSocketDataSerializer::class)
data class WebSocketData(
    val type: String,
    val data: Any
)

enum class WebSocketDataType(val value: String) {
    MESSAGE_REQUEST("messageRequest"),
    MARK_MESSAGE_AS_READ_REQUEST("markMessageAsReadRequest"),
    MESSAGE_RESPONSE("messageResponse"),
    ACTIVE_USER_IDS_RESPONSE("activeUserIdsResponse"),
}
