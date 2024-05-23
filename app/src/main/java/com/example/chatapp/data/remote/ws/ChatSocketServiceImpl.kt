package com.example.chatapp.data.remote.ws

import com.example.chatapp.data.remote.di.SocketHttpClient
import com.example.chatapp.data.remote.response.ActiveUserIdsResponse
import com.example.chatapp.data.remote.response.MessageResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import javax.inject.Inject

sealed class SocketSessionResult {
    data object EmptyResult : SocketSessionResult()
    data class ActiveStatus(val activeUserIdsResponse: ActiveUserIdsResponse) : SocketSessionResult()
    data class MessageReceived(val message: MessageResponse) : SocketSessionResult()
}

class ChatSocketServiceImpl @Inject constructor(
    @SocketHttpClient
    private val client: HttpClient,
) : ChatSocketService {

    private var webSocketSession: WebSocketSession? = null

    override val isConnected: Boolean
        get() = webSocketSession?.isActive == true

    override suspend fun connect(userId: String): Result<Unit> {
        return try {
            // "ws://chat-api.androidmoderno.com.br:8080/chat/$userId"
            // "ws://192.168.1.68:8080/chat/$userId"
            client.webSocketSession("wss://chat-api.androidmoderno.com.br/chat/$userId").run {
                webSocketSession = this
                if (this.isActive) {
                    Result.success(Unit)
                } else Result.failure(Exception("Couldn't establish connection"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun observeSocketResultFlow(): Flow<SocketSessionResult> {
        return try {
            webSocketSession?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map { frame ->
                    val webSocketDataJsonString = (frame as? Frame.Text)?.readText() ?: ""

                    val webSocketData = Json.decodeFromString<WebSocketData>(webSocketDataJsonString)

                    when (webSocketData.type) {
                        WebSocketDataType.MESSAGE_RESPONSE.value -> {
                            SocketSessionResult.MessageReceived(webSocketData.data as MessageResponse)
                        }

                        WebSocketDataType.ACTIVE_USER_IDS_RESPONSE.value -> {
                            SocketSessionResult.ActiveStatus(webSocketData.data as ActiveUserIdsResponse)
                        }

                        else -> SocketSessionResult.EmptyResult
                    }
                } ?: emptyFlow()
        } catch (e: Exception) {
            emptyFlow()
        }
    }

    override suspend fun sendGetConversationsList(userId: String) {
        webSocketSession?.send(Frame.Text("getConversations#$userId"))
    }

    override suspend fun sendJsonStringData(
        dataJsonString: String
    ) {
        webSocketSession?.send(Frame.Text(dataJsonString))
    }

    override suspend fun sendReadMessage(messageId: Int) {
        webSocketSession?.send(Frame.Text("markMessageAsRead#$messageId"))
    }

    override suspend fun disconnect() {
        webSocketSession?.close()
    }
}