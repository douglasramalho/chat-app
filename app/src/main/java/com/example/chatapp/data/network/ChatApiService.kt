package com.example.chatapp.data.network

import com.example.chatapp.data.network.request.AuthRequest
import com.example.chatapp.data.network.request.CreateAccountRequest
import com.example.chatapp.data.network.response.PaginatedConversationResponse
import com.example.chatapp.data.network.response.PaginatedMessageResponse
import com.example.chatapp.data.network.response.TokenResponse
import com.example.chatapp.data.network.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
    ): UserResponse

    @GET("conversations")
    suspend fun getConversations(
        @Header("Authorization") token: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
    ): PaginatedConversationResponse

    @GET("messages/{receiverId}")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("receiverId") receiverId: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
    ): PaginatedMessageResponse

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