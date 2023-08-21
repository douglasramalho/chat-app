package com.example.chatapp.data.repository

import com.example.chatapp.model.Conversation
import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    var conversationsListFlow: Flow<List<Conversation>>
    var usersListFlow: Flow<List<User>>

    suspend fun saveConversationsList(conversations: List<Conversation>)

    suspend fun getConversationsList(): List<Conversation>

    suspend fun getConversationBy(id: String): Conversation?

    suspend fun saveUsersList(users: List<User>)

    suspend fun saveUser(user: User)
}