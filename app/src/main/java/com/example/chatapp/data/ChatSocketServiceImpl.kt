package com.example.chatapp.data

import com.example.chatapp.data.remote.di.SocketHttpClient
import com.example.chatapp.data.remote.request.MessageRequest
import com.example.chatapp.data.remote.response.ConversationResponse
import com.example.chatapp.data.remote.response.MessageResponse
import com.example.chatapp.data.remote.response.OnlineStatusResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.WebSocket
import javax.inject.Inject

sealed class SocketSessionResult {
    object SocketOpen : SocketSessionResult()
    object SocketClosed : SocketSessionResult()
    data class SocketFailure(val throwable: Throwable) : SocketSessionResult()
    data class MessageReceived(val message: MessageResponse) : SocketSessionResult()
    data class ConversationsList(val conversationsList: List<ConversationResponse>) : SocketSessionResult()
    data class OnlineStatus(val onlineStatusResponse: OnlineStatusResponse) : SocketSessionResult()
}

class ChatSocketServiceImpl @Inject constructor(
    @SocketHttpClient
    private val client: HttpClient,
) : ChatSocketService {

    private var webSocket: WebSocket? = null
    private var socket: WebSocketSession? = null

    override suspend fun initSession(userId: String): Result<Unit> {
        return try {
            socket = client.webSocketSession("ws://192.168.1.68:8080/chat/$userId")

            if (socket?.isActive == true) {
                Result.success(Unit)
            } else Result.failure(Throwable("Couldn't establish a connection."))
        } catch (e: Exception) {
            Result.failure(e)
        }

        // Prod: "ws://chat-api.douglasmotta.com.br:8080/chat/$userId"
        /*val request = Request.Builder()
            .url("ws://192.168.1.68:8080/chat/$userId")
            .build()

        callbackFlow {
            webSocket = okHttpClient.newWebSocket(
                request = request,
                listener = object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        super.onOpen(webSocket, response)
                        trySend(SocketSessionResult.SocketOpen)
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        super.onMessage(webSocket, text)
                        if (text.isNotEmpty()) {
                            try {
                                val action = text.substringBefore("#")
                                val response = text.substringAfter("#")
                                when (action) {
                                    "newMessage" -> {
                                        val moshi = Moshi
                                            .Builder()
                                            .add(KotlinJsonAdapterFactory())
                                            .build()

                                        val adapter = moshi.adapter(MessageResponse::class.java)

                                        val messageResponse = adapter.fromJson(response)
                                        messageResponse?.let {
                                            trySend(SocketSessionResult.MessageReceived(it))
                                        }
                                    }
                                    "conversationsList" -> {
                                        val moshi = Moshi
                                            .Builder()
                                            .add(KotlinJsonAdapterFactory())
                                            .build()

                                        val types = Types.newParameterizedType(
                                            List::class.java,
                                            ConversationResponse::class.java
                                        )
                                        val adapter = moshi.adapter<List<ConversationResponse>>(types)

                                        val conversationsResponse = adapter.fromJson(response)
                                        conversationsResponse?.let {
                                            trySend(SocketSessionResult.ConversationsList(it))
                                        }
                                    }
                                    "onlineUserIds" -> {
                                        val moshi = Moshi
                                            .Builder()
                                            .add(KotlinJsonAdapterFactory())
                                            .build()

                                        val adapter = moshi.adapter(OnlineStatusResponse::class.java)

                                        val onlineStatusResponse = adapter.fromJson(response)
                                        onlineStatusResponse?.let {
                                            trySend(SocketSessionResult.OnlineStatus(it))
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                // Ignore
                                Log.d("ChatSocketServiceImpl", "onMessage: $e")
                            }
                        }
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        super.onClosed(webSocket, code, reason)
                        Log.d("ChatSocketServiceImpl", "onMessage: $reason")
                        trySend(SocketSessionResult.SocketClosed)
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?
                    ) {
                        super.onFailure(webSocket, t, response)
                        Log.d("ChatSocketServiceImpl", "onMessage: $t")
                        trySend(SocketSessionResult.SocketFailure(t))
                    }
                }
            )

            awaitClose()
        }.flowOn(dispatcher)*/
    }

    override fun observeNewMessages(): Flow<SocketSessionResult> {
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
                            val message = Json.decodeFromString<MessageResponse>(jsonResponse)
                            SocketSessionResult.MessageReceived(message)
                        } else -> SocketSessionResult.SocketOpen
                    }
                } ?: emptyFlow()
        } catch (e: Exception) {
            emptyFlow()
        }
    }

    override suspend fun sendGetConversationsList(userId: String) {
        webSocket?.send("getConversations#$userId")
    }

    override suspend fun sendGetActiveStatus() {
        webSocket?.send("getActiveStatus")
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
        webSocket?.send("markMessageAsRead#$messageId")
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}