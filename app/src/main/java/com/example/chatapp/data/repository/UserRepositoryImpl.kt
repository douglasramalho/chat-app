package com.example.chatapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.datastore.currentUserDataStore
import com.example.chatapp.data.remote.ChatApiService
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val prefs: SharedPreferences
) : UserRepository {

    override val currentUser: Flow<User?>
        get() = context.currentUserDataStore.data.map { currentUser ->
                currentUser.id?.let {
                    User(
                        id = currentUser.id ?: "",
                        username = currentUser.email ?: "",
                        firstName = currentUser.firstName ?: "",
                        lastName = currentUser.firstName ?: "",
                        profilePictureUrl = currentUser.profilePictureUrl ?: "",
                    )
                }
        }

    override val usersListFlow: Flow<List<User>>
        get() = localDataSource.usersListFlow

    override suspend fun saveCurrentUser(user: User?) {
        context.currentUserDataStore.updateData {
            it.copy(
                id = user?.id,
                firstName = user?.firstName,
                lastName = user?.lastName,
                email = user?.username,
                profilePictureUrl = user?.profilePictureUrl
            )
        }
    }

    override suspend fun getAndStoreUsers() {
        val accessToken = prefs.getString("accessToken", "") ?: ""
        val users = remoteDataSource.getUsers(accessToken).map {
            it.toModel()
        }

        localDataSource.saveUsersList(users)
    }

    override suspend fun getUserFlowBy(userId: String): Flow<User?> {
        val user = usersListFlow.map { usersList ->
            usersList.firstOrNull { it.id == userId }
        }

        if (user.firstOrNull() == null) {
            val accessToken = prefs.getString("accessToken", "") ?: ""
            val userResponse = remoteDataSource.getUser(
                token = accessToken,
                userId = userId
            )

            localDataSource.saveUser(userResponse.toModel())
        }

        return usersListFlow.map { usersList ->
            usersList.firstOrNull { it.id == userId }
        }
    }
}