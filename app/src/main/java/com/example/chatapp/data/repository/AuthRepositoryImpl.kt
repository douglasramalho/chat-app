package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.DataStorePreferencesDataSource
import com.example.chatapp.data.datastore.DataStoreProtoDataSource
import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.request.AuthRequest
import com.example.chatapp.data.network.request.CreateAccountRequest
import com.example.chatapp.data.network.response.toModel
import com.example.chatapp.model.AppError
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val chatSocketRepository: ChatSocketRepository,
    private val dataStoreProtoDataSource: DataStoreProtoDataSource,
    private val dataStorePreferencesDataSource: DataStorePreferencesDataSource,
) : AuthRepository {

    override suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureId: String?,
    ) {
        networkDataSource.signUp(
            request = CreateAccountRequest(
                username = username,
                password = password,
                firstName = firstName,
                lastName = lastName,
                profilePictureId = profilePictureId,
            )
        )
    }

    override suspend fun signIn(username: String, password: String) {
        val tokenResponse = networkDataSource.signIn(
                request = AuthRequest(
                    username = username,
                    password = password
                )
        )

        saveAccessToken(tokenResponse.token)
    }

    private suspend fun saveAccessToken(accessToken: String) {
        dataStorePreferencesDataSource.saveAccessToken(accessToken)
    }

    override suspend fun getAndStoreUserInfo() {
        return if (!isAuthenticated()) {
            throw AppError.ApiError.Unauthorized
        } else {
            val userResponse = networkDataSource.authenticate()
            dataStoreProtoDataSource.saveCurrentUser(userResponse.toModel())
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return !dataStorePreferencesDataSource.accessTokenFlow.firstOrNull().isNullOrEmpty()
    }

    override suspend fun logout() {
        dataStoreProtoDataSource.clear()
        dataStorePreferencesDataSource.clear()
        chatSocketRepository.closeSession()
    }
}