package com.example.chatapp.data.remote

import com.example.chatapp.data.remote.request.AuthRequest
import com.example.chatapp.data.remote.request.CreateAccountRequest
import com.example.chatapp.data.remote.response.ConversationResponse
import com.example.chatapp.data.remote.response.MessageResponse
import com.example.chatapp.data.remote.response.TokenResponse
import com.example.chatapp.data.remote.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiService {

    @POST("signup")
    suspend fun signUp(
        @Body request: CreateAccountRequest,
    )

    @POST("signin")
    suspend fun signIn(
        @Body request: AuthRequest
    ): TokenResponse

    @GET("authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    )

    @GET("conversations/{userId}")
    suspend fun getConversations(@Path("userId") userId: String): List<ConversationResponse>

    @GET("messages/{conversationId}")
    suspend fun getMessages(@Path("conversationId") conversationId: String): List<MessageResponse>

    @GET("messages/{senderId}/{receiverId}")
    suspend fun getMessages2(
        @Path("senderId") senderId: String,
        @Path("receiverId") receiverId: String,
    ): List<MessageResponse>

    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ): List<UserResponse>

    @GET("users/{userId}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
    ): UserResponse
}