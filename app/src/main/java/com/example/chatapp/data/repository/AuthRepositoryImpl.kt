package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.model.AuthResult
import com.example.chatapp.data.remote.ChatApiService
import com.example.chatapp.data.remote.request.AuthRequest
import com.example.chatapp.data.remote.request.CreateAccountRequest
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ChatApiService,
    private val prefs: SharedPreferences
) : AuthRepository {
    override suspend fun signUp(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        profilePictureUrl: String?,
    ): AuthResult<Unit> {
        return try {
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
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else AuthResult.UnknownError()
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun signIn(username: String, password: String): AuthResult<Unit> {
        return try {
            val response = apiService.signIn(
                request = AuthRequest(
                    username = username,
                    password = password
                )
            )

            prefs.edit()
                .putString("accessToken", response.token)
                .apply()

            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else AuthResult.UnknownError()
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return try {
            val accessToken =
                prefs.getString("accessToken", null) ?: AuthResult.Unauthorized<Unit>()
            apiService.authenticate("Bearer $accessToken")
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else AuthResult.UnknownError()
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun logout() {
        prefs.edit()
            .clear()
            .apply()
    }
}