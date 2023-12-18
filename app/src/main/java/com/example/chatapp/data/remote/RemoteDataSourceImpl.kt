package com.example.chatapp.data.remote

import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.remote.di.ApiHttpClient
import com.example.chatapp.data.remote.request.AuthRequest
import com.example.chatapp.data.remote.request.CreateAccountRequest
import com.example.chatapp.data.remote.resource.ConversationsResource
import com.example.chatapp.data.remote.resource.MessagesResource
import com.example.chatapp.data.remote.resource.UsersResource
import com.example.chatapp.data.remote.response.PaginatedConversationResponse
import com.example.chatapp.data.remote.response.PaginatedMessageResponse
import com.example.chatapp.data.remote.response.TokenResponse
import com.example.chatapp.data.remote.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    @ApiHttpClient
    private val httpClient: HttpClient,
) : RemoteDataSource {

    override suspend fun signUp(request: CreateAccountRequest) {
        httpClient.post("signup") {
            setBody(request)
        }
    }

    override suspend fun signIn(request: AuthRequest): TokenResponse {
        return httpClient.post("signin") {
            setBody(request)
        }.body()
    }

    override suspend fun authenticate(): UserResponse {
        return safeApiCall {
            httpClient.get("authenticate").body()
        }
    }

    override suspend fun getConversations(): PaginatedConversationResponse {
        return safeApiCall {
            httpClient.get(ConversationsResource.GetPaginated()).body()
        }
    }

    override suspend fun getMessages(receiverId: String): PaginatedMessageResponse {
        return httpClient.get(MessagesResource.Messages(receiverId = receiverId)).body()
    }

    override suspend fun getUsers(): List<UserResponse> {
        return safeApiCall {
            httpClient.get(UsersResource.Users()).body()
        }
    }

    override suspend fun getUser(userId: String): UserResponse {
        return safeApiCall {
            httpClient.get(UsersResource.Users.Id(id = userId)).body()
        }
    }
}