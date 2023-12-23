package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.DataStorePreferencesDataSource
import com.example.chatapp.data.datastore.DataStoreProtoDataSource
import com.example.chatapp.data.extension.errorMapping
import com.example.chatapp.data.network.MyHttpException
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
import kotlinx.coroutines.flow.lastOrNull
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
        profilePictureUrl: String?,
    ): Flow<ResultStatus<Unit>> {
        val signUpResult = getFlowResult {
            networkDataSource.signUp(
                request = CreateAccountRequest(
                    username = username,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    profilePictureUrl = profilePictureUrl,
                )
            )
        }

        return if (signUpResult.lastOrNull() is ResultStatus.Success) {
            signIn(username, password)
        } else signUpResult
    }

    override suspend fun signIn(username: String, password: String): Flow<ResultStatus<Unit>> {
        val signResult = getFlowResult {
            networkDataSource.signIn(
                request = AuthRequest(
                    username = username,
                    password = password
                )
            )
        }.lastOrNull()

        return if (signResult is ResultStatus.Success) {
            dataStorePreferencesDataSource.saveAccessToken(signResult.data.token)
            authenticate()
        } else flowOf(ResultStatus.Error(AppError.ApiError.Unauthorized))
    }

    override suspend fun authenticate(): Flow<ResultStatus<Unit>> {
        return try {
            if (!isAuthenticated()) {
                flowOf(ResultStatus.Error(AppError.ApiError.Unauthorized))
            } else {
                val authenticateResult = getFlowResult {
                    networkDataSource.authenticate()
                }

                when (val result = authenticateResult.lastOrNull()) {
                    is ResultStatus.Success -> {
                        val userResponse = result.data
                        dataStoreProtoDataSource.saveCurrentUser(userResponse.toModel())
                        flowOf(ResultStatus.Success(Unit))
                    }
                    else -> flowOf(ResultStatus.Success(Unit))
                }
            }
        } catch (e: MyHttpException) {
            if (e.code == 401) {
                logout()
            }

            flowOf(ResultStatus.Error(e.errorMapping()))
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