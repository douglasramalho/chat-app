package com.example.chatapp.data.repository

import com.example.chatapp.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureUrl: String?,
    ): Flow<Result<Unit>>

    suspend fun signIn(username: String, password: String): Flow<Result<Unit>>

    suspend fun authenticate(): Result<Unit>

    suspend fun logout()
}