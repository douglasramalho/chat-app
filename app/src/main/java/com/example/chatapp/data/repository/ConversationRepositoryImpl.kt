package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.remote.response.toConversation
import com.example.chatapp.model.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreferences: SharedPreferences,
) : ConversationRepository {

    override suspend fun getConversations(): Flow<List<Conversation>> {
        val accessToken = sharedPreferences.getString("accessToken", null)
        return accessToken?.let { token ->
            val jwt = JWT(token)
            val currentUserId = jwt.claims["userId"]?.asString() ?: ""
            val response = remoteDataSource.getConversations()

            flowOf(
                response.conversations.map {
                    it.toConversation(currentUserId)
                }
            )
        } ?: flowOf()
    }
}