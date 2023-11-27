package com.example.chatapp.data

import com.example.chatapp.data.remote.ChatApiService
import com.example.chatapp.data.remote.response.ConversationResponse
import com.example.chatapp.data.remote.response.MessageResponse
import com.example.chatapp.data.remote.response.UserResponse
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ChatApiService
) : RemoteDataSource {

    override suspend fun getConversations(userId: String): List<ConversationResponse> {
        return apiService.getConversations(userId)
    }

    override suspend fun getMessages(token: String, receiverId: String): List<MessageResponse> {
        return apiService.getMessages("Bearer $token", receiverId).messages
    }

    override suspend fun getUsers(token: String): List<UserResponse> {
        return apiService.getUsers(token)
    }
}