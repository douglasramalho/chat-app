package com.example.chatapp.data.ws

import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun openSession(userId: String)

    fun observeSocketResultFlow(): Flow<SocketSessionResult>

    suspend fun sendGetConversationsList(userId: String)

    suspend fun sendGetActiveStatus()

    suspend fun sendMessage(
        receiverId: String,
        message: String
    )

    suspend fun sendReadMessage(messageId: Int)

    suspend fun closeSession()
}