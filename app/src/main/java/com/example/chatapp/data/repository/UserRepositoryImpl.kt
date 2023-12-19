package com.example.chatapp.data.repository

import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.datastore.CurrentUser
import com.example.chatapp.data.datastore.DataStoreStorage
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.data.util.getFlowResult
import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataStoreStorage: DataStoreStorage,
    private val remoteDataSource: RemoteDataSource,
) : UserRepository {

    override val currentUser: Flow<User?>
        get() = dataStoreStorage.currentUser.map { currentUser ->
            currentUser.id?.let { id ->
                User(
                    id = id,
                    username = currentUser.email ?: "",
                    firstName = currentUser.firstName ?: "",
                    lastName = currentUser.firstName ?: "",
                    profilePictureUrl = currentUser.profilePictureUrl ?: "",
                )
            }
        }

    override suspend fun saveCurrentUser(user: User?) {
        user?.let {
            dataStoreStorage.saveCurrentUser(
                CurrentUser(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.username,
                    profilePictureUrl = it.profilePictureUrl,
                )
            )
        }
    }

    override suspend fun getUsers(): Flow<ResultStatus<List<User>>> {
        return getFlowResult {
            remoteDataSource.getUsers().map {
                it.toModel()
            }
        }
    }

    override suspend fun getUser(userId: String): Flow<ResultStatus<User>> {
        return getFlowResult {
            remoteDataSource.getUser(
                userId = userId
            ).toModel()
        }

    }
}