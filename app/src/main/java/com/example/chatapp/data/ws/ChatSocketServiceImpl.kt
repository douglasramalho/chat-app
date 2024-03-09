package com.example.chatapp.data.ws

import com.example.chatapp.data.network.di.SocketHttpClient
import com.example.chatapp.data.network.request.MessageRequest
import com.example.chatapp.data.network.response.ActiveStatusResponse
import com.example.chatapp.data.network.response.MessageResponse
import com.example.chatapp.data.network.response.UnreadStatusResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

sealed class SocketSessionResult {
    data object EmptyResult : SocketSessionResult()
    data class ActiveStatus(val activeStatusResponse: ActiveStatusResponse) : SocketSessionResult()
    data class UnreadStatus(val unreadStatusResponse: UnreadStatusResponse) : SocketSessionResult()
    data class MessageReceived(val message: MessageResponse) : SocketSessionResult()
}

class ChatSocketServiceImpl @Inject constructor(
    @SocketHttpClient
    private val client: HttpClient,
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun openSession(userId: String) {
        try {
            socket = client.webSocketSession("ws://chat-api.androidmoderno.com.br:8080/chat/$userId")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeSocketResultFlow(): Flow<SocketSessionResult> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map { frame ->
                    val text = (frame as? Frame.Text)?.readText() ?: ""

                    val action = text.substringBefore("#")
                    val jsonResponse = text.substringAfter("#")

                    when (action) {
                        "newMessage" -> {
                            val messageResponse =
                                Json.decodeFromString<MessageResponse>(jsonResponse)
                            SocketSessionResult.MessageReceived(messageResponse)
                        }

                        "unreadStatus" -> {
                            val unreadStatusResponse =
                                Json.decodeFromString<UnreadStatusResponse>(jsonResponse)
                            SocketSessionResult.UnreadStatus(unreadStatusResponse)
                        }

                        "activeUserIds" -> {
                            val onlineStatusResponse =
                                Json.decodeFromString<ActiveStatusResponse>(jsonResponse)
                            SocketSessionResult.ActiveStatus(onlineStatusResponse)
                        }

                        else -> SocketSessionResult.EmptyResult
                    }
                } ?: emptyFlow()
        } catch (e: Exception) {
            emptyFlow()
        }
    }

    override suspend fun sendGetConversationsList(userId: String) {
        socket?.send(Frame.Text("getConversations#$userId"))
    }

    override suspend fun sendGetActiveStatus() {
        socket?.send(Frame.Text("getActiveStatus"))
    }

    override suspend fun sendMessage(
        receiverId: String,
        message: String
    ) {
        val messageRequest = MessageRequest(
            receiverId = receiverId,
            text = message
        )

        val jsonText = Json.encodeToString(messageRequest)
        socket?.send(Frame.Text("newMessage#$jsonText"))
    }

    override suspend fun sendReadMessage(messageId: Int) {
        socket?.send(Frame.Text("markMessageAsRead#$messageId"))
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}