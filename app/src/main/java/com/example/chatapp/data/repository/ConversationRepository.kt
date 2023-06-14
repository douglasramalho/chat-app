package com.example.chatapp.data.repository

import com.example.chatapp.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {

    suspend fun getConversations(): Flow<List<Conversation>>
}