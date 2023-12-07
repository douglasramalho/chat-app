package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
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
            val response = remoteDataSource.getConversations(token)

            flowOf(
                response.conversations.map {
                    Conversation(
                        id = it.id,
                        members = it.members.map { member ->
                            ConversationMember(
                                id = member.id,
                                isSelf = currentUserId == member.id,
                                username = member.username,
                                firstName = member.firstName,
                                lastName = member.lastName,
                                profilePictureUrl = member.profilePictureUrl,
                            )
                        },
                        unreadCount = it.unreadCount,
                        lastMessage = it.lastMessage,
                        timestamp = "9:00"
                    )
                }
            )
        } ?: flowOf()
    }
}