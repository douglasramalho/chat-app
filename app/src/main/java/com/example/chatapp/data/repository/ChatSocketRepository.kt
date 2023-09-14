package com.example.chatapp.data.repository

import com.example.chatapp.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ChatSocketRepository {

    val conversationsListFlow: Flow<List<Conversation>>

    suspend fun openSession(): Flow<SocketResult?>

    suspend fun closeSession()

    suspend fun getConversationsList()

    suspend fun sendMessage(
        receiverId: String,
        message: String
    )

    suspend fun sendReadMessage(messageId: String)

    suspend fun getConversationBy(conversationId: String): Flow<Conversation?>
}