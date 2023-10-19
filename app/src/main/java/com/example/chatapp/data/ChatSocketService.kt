package com.example.chatapp.data

import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        userId: String
    ): Flow<SocketSessionResult>

    suspend fun sendGetConversationsList(userId: String)

    suspend fun sendGetOnlineStatus()

    suspend fun sendMessage(
        receiverId: String,
        message: String
    )

    suspend fun sendReadMessage(messageId: String)

    suspend fun closeSession()
}