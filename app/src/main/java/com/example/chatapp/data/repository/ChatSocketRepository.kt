package com.example.chatapp.data.repository

import com.example.chatapp.model.Conversation
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ChatSocketRepository {

    val conversationsListFlow: Flow<List<Conversation>>

    val messagesFlow: MutableStateFlow<Message?>

    suspend fun openSession(): Flow<SocketResult?>

    suspend fun closeSession()

    suspend fun getConversationsList()

    suspend fun getOnlineStatus()

    suspend fun sendMessage(
        receiverId: String,
        message: String
    )

    suspend fun sendReadMessage(messageId: Int)

    suspend fun getConversationBy(conversationId: String): Flow<Conversation?>
}