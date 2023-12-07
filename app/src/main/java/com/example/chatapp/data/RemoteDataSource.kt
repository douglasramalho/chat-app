package com.example.chatapp.data

import com.example.chatapp.data.remote.request.AuthRequest
import com.example.chatapp.data.remote.request.CreateAccountRequest
import com.example.chatapp.data.remote.response.PaginatedConversationResponse
import com.example.chatapp.data.remote.response.PaginatedMessageResponse
import com.example.chatapp.data.remote.response.TokenResponse
import com.example.chatapp.data.remote.response.UserResponse

interface RemoteDataSource {

    suspend fun signUp(request: CreateAccountRequest)

    suspend fun signIn(request: AuthRequest): TokenResponse

    suspend fun authenticate(token: String): UserResponse

    suspend fun getConversations(token: String): PaginatedConversationResponse

    suspend fun getMessages(token: String, receiverId: String): PaginatedMessageResponse

    suspend fun getUsers(token: String): List<UserResponse>

    suspend fun getUser(token: String, userId: String): UserResponse
}