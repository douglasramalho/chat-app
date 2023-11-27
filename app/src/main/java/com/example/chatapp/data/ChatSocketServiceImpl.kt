package com.example.chatapp.data

import android.util.Log
import com.example.chatapp.data.remote.request.MessageRequest
import com.example.chatapp.data.remote.response.ConversationResponse
import com.example.chatapp.data.remote.response.MessageResponse
import com.example.chatapp.data.remote.response.OnlineStatusResponse
import com.example.chatapp.di.DefaultDispatcher
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
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
    private val okHttpClient: OkHttpClient,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : ChatSocketService {

    private var webSocket: WebSocket? = null

    override suspend fun initSession(userId: String): Flow<SocketSessionResult> {
        // Prod: "ws://chat-api.douglasmotta.com.br:8080/chat/$userId"
        val request = Request.Builder()
            .url("ws://192.168.1.68:8080/chat/$userId")
            .build()

        return callbackFlow {
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
                        trySend(SocketSessionResult.SocketClosed)
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?
                    ) {
                        super.onFailure(webSocket, t, response)
                        trySend(SocketSessionResult.SocketFailure(t))
                    }
                }
            )

            awaitClose()
        }.flowOn(dispatcher)
    }

    override suspend fun sendGetConversationsList(userId: String) {
        webSocket?.send("getConversations#$userId")
    }

    override suspend fun sendGetOnlineStatus() {
        webSocket?.send("getOnlineStatus")
    }

    override suspend fun sendMessage(
        receiverId: String,
        message: String
    ) {
        val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(MessageRequest::class.java)
        val messageRequest = MessageRequest(
            receiverId = receiverId,
            text = message
        )
        val jsonText = adapter.toJson(messageRequest)
        webSocket?.send("newMessage#$jsonText")
    }

    override suspend fun sendReadMessage(messageId: String) {
        webSocket?.send("markMessageAsRead#$messageId")
    }

    override suspend fun closeSession() {
        webSocket?.close(1001, "Closed by app")
    }
}