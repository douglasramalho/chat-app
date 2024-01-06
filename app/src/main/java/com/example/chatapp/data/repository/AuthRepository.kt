package com.example.chatapp.data.repository

import com.example.chatapp.data.util.ResultStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureId: String?,
    ): Flow<ResultStatus<Unit>>

    suspend fun signIn(username: String, password: String): Flow<ResultStatus<String>>

    suspend fun authenticate(): Flow<ResultStatus<Unit>>

    suspend fun isAuthenticated(): Boolean

    suspend fun logout()
}