package com.example.chatapp.data.repository

import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {

    suspend fun getConversations(): Flow<ResultStatus<List<Conversation>>>
}