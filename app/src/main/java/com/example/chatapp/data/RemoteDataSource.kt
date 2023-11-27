package com.example.chatapp.data

import com.example.chatapp.data.remote.response.ConversationResponse
import com.example.chatapp.data.remote.response.MessageResponse
import com.example.chatapp.data.remote.response.UserResponse

interface RemoteDataSource {

    suspend fun getConversations(userId: String): List<ConversationResponse>

    suspend fun getMessages(token: String, receiverId: String): List<MessageResponse>

    suspend fun getUsers(token: String): List<UserResponse>
}