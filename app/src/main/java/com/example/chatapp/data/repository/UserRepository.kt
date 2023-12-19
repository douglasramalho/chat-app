package com.example.chatapp.data.repository

import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    val currentUser: Flow<User?>

    suspend fun saveCurrentUser(user: User?)

    suspend fun getUsers(): Flow<ResultStatus<List<User>>>

    suspend fun getUser(userId: String): Flow<ResultStatus<User>>
}