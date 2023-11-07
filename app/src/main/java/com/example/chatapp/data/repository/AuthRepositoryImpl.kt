package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.data.extension.errorMapping
import com.example.chatapp.data.remote.ChatApiService
import com.example.chatapp.data.remote.request.AuthRequest
import com.example.chatapp.data.remote.request.CreateAccountRequest
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.model.AppError
import com.example.chatapp.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ChatApiService,
    private val prefs: SharedPreferences,
    private val userRepository: UserRepository,
    private val localDataSource: LocalDataSource,
) : AuthRepository {

    override suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureUrl: String?,
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        apiService.signUp(
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
        val response = apiService.signIn(
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
        val accessToken =
            prefs.getString("accessToken", null)
                ?: return Result.Error(AppError.ApiError.Unauthorized)

        return try {
            val userResponse = apiService.authenticate("Bearer $accessToken")
            val user = userResponse.toModel()

            userRepository.saveCurrentUser(user)

            Result.Success(Unit)
        } catch (e: HttpException) {
            Result.Error(e.errorMapping())
        }
    }

    override suspend fun logout() {
        localDataSource.clear()

        prefs.edit()
            .clear()
            .apply()
    }
}