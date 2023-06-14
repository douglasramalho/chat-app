package com.example.chatapp.data

import com.example.chatapp.data.remote.ChatApiService
import com.example.chatapp.data.remote.response.ConversationResponse
import com.example.chatapp.data.remote.response.MessageResponse
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ChatApiService
) : RemoteDataSource {

    override suspend fun getConversations(userId: String): List<ConversationResponse> {
        return apiService.getConversations(userId)
    }

    override suspend fun getMessages(conversationId: String): List<MessageResponse> {
        return apiService.getMessages(conversationId)
    }
}