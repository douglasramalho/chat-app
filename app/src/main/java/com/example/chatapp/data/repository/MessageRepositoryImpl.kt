package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.remote.response.toMessage
import com.example.chatapp.data.repository.extension.getUserIdFromToken
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreferences: SharedPreferences,
) : MessageRepository {

    override suspend fun getMessages(conversationId: String): Flow<List<Message>> {
        val accessToken = sharedPreferences.getString("accessToken", null)
        val userId = accessToken.getUserIdFromToken()
        return flowOf(
            remoteDataSource.getMessages(conversationId).map {
                it.toMessage(userId)
            }
        )
    }

    override suspend fun getMessages2(receiverId: String): Flow<List<Message>> {
        val accessToken = sharedPreferences.getString("accessToken", null)
        val userId = accessToken.getUserIdFromToken()
        return flowOf(
            remoteDataSource.getMessages(userId, receiverId).map {
                it.toMessage(userId)
            }
        )
    }
}