package com.example.chatapp.data

import com.example.chatapp.data.remote.response.ConversationResponse
import com.example.chatapp.data.remote.response.MessageResponse

interface RemoteDataSource {

    suspend fun getConversations(userId: String): List<ConversationResponse>

    suspend fun getMessages(conversationId: String): List<MessageResponse>
}