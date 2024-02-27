package com.example.chatapp.data.network

import com.example.chatapp.data.network.request.AuthRequest
import com.example.chatapp.data.network.request.CreateAccountRequest
import com.example.chatapp.data.network.request.RegisterPushRequest
import com.example.chatapp.data.network.response.ImageResponse
import com.example.chatapp.data.network.response.PaginatedConversationResponse
import com.example.chatapp.data.network.response.PaginatedMessageResponse
import com.example.chatapp.data.network.response.TokenResponse
import com.example.chatapp.data.network.response.UserResponse

interface NetworkDataSource {

    suspend fun signUp(request: CreateAccountRequest)

    suspend fun signIn(request: AuthRequest): TokenResponse

    suspend fun authenticate(): UserResponse

    suspend fun registerNotifications(request: RegisterPushRequest)

    suspend fun getConversations(): PaginatedConversationResponse

    suspend fun getMessages(receiverId: String): PaginatedMessageResponse

    suspend fun uploadProfilePicture(filePath: String): ImageResponse

    suspend fun getUsers(): List<UserResponse>

    suspend fun getUser(userId: String): UserResponse
}