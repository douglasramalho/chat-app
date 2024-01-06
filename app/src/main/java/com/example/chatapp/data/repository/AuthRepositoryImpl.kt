package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.DataStorePreferencesDataSource
import com.example.chatapp.data.datastore.DataStoreProtoDataSource
import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.request.AuthRequest
import com.example.chatapp.data.network.request.CreateAccountRequest
import com.example.chatapp.data.network.response.toModel
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.data.util.getFlowResult
import com.example.chatapp.model.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val dataStoreProtoDataSource: DataStoreProtoDataSource,
    private val dataStorePreferencesDataSource: DataStorePreferencesDataSource,
) : AuthRepository {

    override suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureId: String?,
    ): Flow<ResultStatus<Unit>> {
        return getFlowResult {
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
    }

    override suspend fun signIn(username: String, password: String): Flow<ResultStatus<String>> {
        return getFlowResult {
            val response = networkDataSource.signIn(
                request = AuthRequest(
                    username = username,
                    password = password
                )
            )

            response.token
        }.map {
            if (it is ResultStatus.Success) {
                val accessToken = it.data
                saveAccessToken(accessToken)
            }

            it
        }
    }

    private suspend fun saveAccessToken(accessToken: String) {
        dataStorePreferencesDataSource.saveAccessToken(accessToken)
    }

    override suspend fun authenticate(): Flow<ResultStatus<Unit>> {
        return if (!isAuthenticated()) {
            flowOf(ResultStatus.Error(AppError.ApiError.Unauthorized))
        } else {
            val authenticateResult = getFlowResult {
                networkDataSource.authenticate()
            }

            when (val result = authenticateResult.last()) {
                is ResultStatus.Success -> {
                    val userResponse = result.data
                    dataStoreProtoDataSource.saveCurrentUser(userResponse.toModel())
                    flowOf(ResultStatus.Success(Unit))
                }

                else -> flowOf(result as ResultStatus.Error)
            }
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return !dataStorePreferencesDataSource.accessTokenFlow.firstOrNull().isNullOrEmpty()
    }

    override suspend fun logout() {
        dataStoreProtoDataSource.clear()
        dataStorePreferencesDataSource.clear()
    }
}