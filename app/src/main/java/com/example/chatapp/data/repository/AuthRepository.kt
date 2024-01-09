package com.example.chatapp.data.repository

import com.example.chatapp.data.util.ResultStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val authenticateStatusFlow: Flow<ResultStatus<Unit>>

    suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureId: String?,
    )

    suspend fun signIn(username: String, password: String)

    suspend fun getAndStoreUserInfo()

    suspend fun isAuthenticated(): Boolean

    suspend fun logout()
}