package com.example.chatapp.data.repository

import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ChatSocketRepository {

    val messagesFlow: MutableStateFlow<Message?>

    suspend fun openSession(openSocketCallback: (isOpen: Boolean) -> Unit): Flow<SocketResult>

    suspend fun closeSession()

    suspend fun getConversationsList()

    suspend fun getOnlineStatus()

    suspend fun sendMessage(
        receiverId: String,
        message: String
    )

    suspend fun sendReadMessage(messageId: Int)
}