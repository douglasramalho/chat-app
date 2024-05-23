package com.example.chatapp.data.remote.ws

import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    val isConnected: Boolean

    suspend fun connect(userId: String): Result<Unit>

    fun observeSocketResultFlow(): Flow<SocketSessionResult>

    suspend fun sendGetConversationsList(userId: String)

    suspend fun sendJsonStringData(
        dataJsonString: String
    )

    suspend fun sendReadMessage(messageId: Int)

    suspend fun disconnect()
}