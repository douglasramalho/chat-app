package com.example.chatapp.data.repository

import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ChatSocketRepository {

    val messagesFlow: MutableStateFlow<Message?>

    suspend fun openSession(): Result<Unit>

    suspend fun observeSocketResult(): Flow<SocketResult>

    suspend fun closeSession()

    suspend fun sendMessage(
        receiverId: String,
        text: String,
        timestamp: Long,
    )

    suspend fun sendReadMessage(messageId: Int)
}