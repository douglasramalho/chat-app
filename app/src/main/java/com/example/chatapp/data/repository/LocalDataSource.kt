package com.example.chatapp.data.repository

import com.example.chatapp.model.Conversation
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    var conversationsListFlow: Flow<List<Conversation>>

    suspend fun saveConversationsList(conversations: List<Conversation>)

    suspend fun getConversationsList(): List<Conversation>

    suspend fun getConversationBy(id: String): Conversation?
}