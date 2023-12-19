package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.data.repository.extension.getUserIdFromToken
import com.example.chatapp.model.AppError
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreferences: SharedPreferences,
) : MessageRepository {

    override suspend fun getMessages(receiverId: String): Flow<List<Message>> {
        val accessToken = sharedPreferences.getString("accessToken", null)
            ?: throw Exception(AppError.ApiError.Unauthorized)

        val userId = accessToken.getUserIdFromToken()

        return try {
            flowOf(
                remoteDataSource.getMessages(receiverId)
                    .messages
                    .map {
                        it.toModel(userId)
                    }
            )
        } catch (e: Throwable) {
            flowOf()
        }
    }
}