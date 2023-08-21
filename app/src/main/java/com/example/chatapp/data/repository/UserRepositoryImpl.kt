package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.data.remote.ChatApiService
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ChatApiService,
    private val localDataSource: LocalDataSource,
    private val prefs: SharedPreferences
) : UserRepository {

    override val usersListFlow: Flow<List<User>>
        get() = localDataSource.usersListFlow

    override suspend fun getAndStoreUsers() {
        val accessToken = prefs.getString("accessToken", "")
        val users = apiService.getUsers("Bearer ${accessToken!!}").map {
            it.toModel()
        }

        localDataSource.saveUsersList(users)
    }

    override suspend fun getUserFlowBy(userId: String): Flow<User?> {
        val user = usersListFlow.map { usersList ->
            usersList.firstOrNull { it.id == userId }
        }

        if (user.firstOrNull() == null) {
            val accessToken = prefs.getString("accessToken", "")
            val userResponse = apiService.getUserById(
                token = "Bearer ${accessToken!!}",
                userId = userId
            )

            localDataSource.saveUser(userResponse.toModel())
        }

        return usersListFlow.map { usersList ->
            usersList.firstOrNull { it.id == userId }
        }
    }
}