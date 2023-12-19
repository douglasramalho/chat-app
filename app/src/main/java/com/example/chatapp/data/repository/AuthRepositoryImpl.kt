package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.extension.errorMapping
import com.example.chatapp.data.remote.MyHttpException
import com.example.chatapp.data.remote.request.AuthRequest
import com.example.chatapp.data.remote.request.CreateAccountRequest
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.model.AppError
import com.example.chatapp.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val prefs: SharedPreferences,
    private val userRepository: UserRepository,
) : AuthRepository {

    override suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureUrl: String?,
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        remoteDataSource.signUp(
            request = CreateAccountRequest(
                username = username,
                password = password,
                firstName = firstName,
                lastName = lastName,
                profilePictureUrl = profilePictureUrl,
            )
        )

        signIn(username, password)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it.errorMapping()))
    }

    override suspend fun signIn(username: String, password: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        val response = remoteDataSource.signIn(
            request = AuthRequest(
                username = username,
                password = password
            )
        )

        prefs.edit()
            .putString("accessToken", response.token)
            .apply()

        authenticate()

        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it.errorMapping()))
    }

    override suspend fun authenticate(): Result<Unit> {
        val accessToken = prefs.getString("accessToken", null)

        return if (accessToken == null) {
            logout()
            Result.Error(AppError.ApiError.Unauthorized)
        } else {
            try {
                val userResponse = remoteDataSource.authenticate()
                val user = userResponse.toModel()

                userRepository.saveCurrentUser(user)

                Result.Success(Unit)
            } catch (e: MyHttpException) {
                if (e.code == 401) {
                    logout()
                }

                Result.Error(e.errorMapping())
            }
        }
    }

    override suspend fun logout() {
        userRepository.saveCurrentUser(null)

        prefs.edit()
            .clear()
            .apply()
    }
}