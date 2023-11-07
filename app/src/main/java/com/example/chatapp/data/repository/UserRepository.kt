package com.example.chatapp.data.repository

import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    val currentUser: Flow<User?>

    val usersListFlow: Flow<List<User>>

    suspend fun saveCurrentUser(user: User)

    suspend fun getAndStoreUsers()

    suspend fun getUserFlowBy(userId: String): Flow<User?>
}