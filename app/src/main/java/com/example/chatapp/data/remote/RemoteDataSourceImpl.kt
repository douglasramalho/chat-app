package com.example.chatapp.data.remote

import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.remote.di.ApiHttpClient
import com.example.chatapp.data.remote.request.AuthRequest
import com.example.chatapp.data.remote.request.CreateAccountRequest
import com.example.chatapp.data.remote.response.PaginatedConversationResponse
import com.example.chatapp.data.remote.response.PaginatedMessageResponse
import com.example.chatapp.data.remote.response.TokenResponse
import com.example.chatapp.data.remote.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters
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

    override suspend fun authenticate(token: String): UserResponse {
        return safeApiCall {
            httpClient.get("authenticate").body()
        }
    }

    override suspend fun getConversations(token: String): PaginatedConversationResponse {
        return safeApiCall {
            httpClient.get("conversations") {
                parameters {
                    append("offset", "0")
                    append("limit", "10")
                }
            }.body()
        }
    }

    override suspend fun getMessages(token: String, receiverId: String): PaginatedMessageResponse {
        return httpClient.get("messages/$receiverId") {
            parameters {
                append("offset", "0")
                append("limit", "10")
            }
        }.body()
    }

    override suspend fun getUsers(token: String): List<UserResponse> {
        return safeApiCall {
            httpClient.get("users") {
                parameters {
                    append("offset", "0")
                    append("limit", "10")
                }
            }.body()
        }
    }

    override suspend fun getUser(token: String, userId: String): UserResponse {
        return safeApiCall {
            httpClient.get("users/$userId").body()
        }
    }
}