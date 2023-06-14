package com.example.chatapp.data.repository

import com.example.chatapp.model.AuthResult

interface AuthRepository {

    suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureUrl: String?,
    ): AuthResult<Unit>

    suspend fun signIn(username: String, password: String): AuthResult<Unit>

    suspend fun authenticate(): AuthResult<Unit>

    suspend fun logout()
}