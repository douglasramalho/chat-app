package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.remote.response.toMessage
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

        return try {
            flowOf(
                remoteDataSource.getMessages(accessToken, receiverId)
                    .map {
                        it.toMessage(it.senderId)
                    }
            )
        } catch (e: Throwable) {
            flowOf()
        }
    }
}