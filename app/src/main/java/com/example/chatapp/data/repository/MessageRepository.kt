package com.example.chatapp.data.repository

import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun getMessages(conversationId: String): Flow<List<Message>>

    suspend fun getMessages2(receiverId: String): Flow<List<Message>>
}