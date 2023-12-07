package com.example.chatapp.data

import com.example.chatapp.data.remote.response.MessageResponse
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        userId: String
    ): Result<Unit>

    fun observeNewMessages(): Flow<SocketSessionResult>

    suspend fun sendGetConversationsList(userId: String)

    suspend fun sendGetActiveStatus()

    suspend fun sendMessage(
        receiverId: String,
        message: String
    )

    suspend fun sendReadMessage(messageId: Int)

    suspend fun closeSession()
}