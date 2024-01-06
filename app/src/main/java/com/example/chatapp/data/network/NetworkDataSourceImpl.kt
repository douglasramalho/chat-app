package com.example.chatapp.data.network

import com.example.chatapp.data.network.di.ApiHttpClient
import com.example.chatapp.data.network.request.AuthRequest
import com.example.chatapp.data.network.request.CreateAccountRequest
import com.example.chatapp.data.network.resource.ConversationsResource
import com.example.chatapp.data.network.resource.MessagesResource
import com.example.chatapp.data.network.resource.UsersResource
import com.example.chatapp.data.network.response.ImageResponse
import com.example.chatapp.data.network.response.PaginatedConversationResponse
import com.example.chatapp.data.network.response.PaginatedMessageResponse
import com.example.chatapp.data.network.response.TokenResponse
import com.example.chatapp.data.network.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(
    @ApiHttpClient
    private val httpClient: HttpClient,
) : NetworkDataSource {

    override suspend fun signUp(request: CreateAccountRequest) {
        return safeApiCall {
            httpClient.post("signup") {
                setBody(request)
            }
        }
    }

    override suspend fun signIn(request: AuthRequest): TokenResponse {
        return safeApiCall {
            httpClient.post("signin") {
                setBody(request)
            }.body()
        }
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
        return safeApiCall {
            httpClient.get(MessagesResource.Messages(receiverId = receiverId)).body()
        }
    }

    override suspend fun uploadProfilePicture(filePath: String): ImageResponse {
        val file = File(filePath)
        return safeApiCall {
            httpClient.submitFormWithBinaryData(
                url = "profile-picture",
                formData = formData {
                    append("profilePicture", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/${file.extension}")
                        append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                    })
                }
            ).body()
        }
    }

    override suspend fun getUsers(): List<UserResponse> {
        return safeApiCall {
            httpClient.get(UsersResource.Users()).body()
        }
    }

    override suspend fun getUser(userId: String): UserResponse {
        return safeApiCall {
            httpClient.get(UsersResource.ById(id = userId)).body()
        }
    }
}