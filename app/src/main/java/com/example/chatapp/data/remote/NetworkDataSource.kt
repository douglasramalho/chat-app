package com.example.chatapp.data.remote

import com.example.chatapp.data.remote.request.AuthRequest
import com.example.chatapp.data.remote.request.CreateAccountRequest
import com.example.chatapp.data.remote.request.RegisterPushRequest
import com.example.chatapp.data.remote.response.ImageResponse
import com.example.chatapp.data.remote.response.PaginatedConversationResponse
import com.example.chatapp.data.remote.response.PaginatedMessageResponse
import com.example.chatapp.data.remote.response.TokenResponse
import com.example.chatapp.data.remote.response.UserResponse

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