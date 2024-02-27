package com.example.chatapp.data.repository

import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    val messagesFlow: Flow<List<Message>>

    suspend fun getAndStoreMessages(receiverId: String)

    suspend fun saveNewMessage(message: Message)
}