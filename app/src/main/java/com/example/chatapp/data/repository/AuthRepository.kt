package com.example.chatapp.data.repository

interface AuthRepository {

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