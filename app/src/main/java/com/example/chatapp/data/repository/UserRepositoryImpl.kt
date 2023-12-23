package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.DataStoreProtoDataSource
import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.response.toModel
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.data.util.getFlowResult
import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataStoreProtoDataSource: DataStoreProtoDataSource,
    private val networkDataSource: NetworkDataSource,
) : UserRepository {

    override val currentUser: Flow<User?>
        get() = dataStoreProtoDataSource.currentUser.map { currentUser ->
            if (currentUser.id.isNotEmpty()) {
                User(
                    id = currentUser.id,
                    username = currentUser.email,
                    firstName = currentUser.firstName,
                    lastName = currentUser.firstName,
                    profilePictureUrl = currentUser.profilePictureUrl,
                )
            } else null
        }

    override suspend fun getUsers(): Flow<ResultStatus<List<User>>> {
        return getFlowResult {
            networkDataSource.getUsers().map {
                it.toModel()
            }
        }
    }

    override suspend fun getUser(userId: String): Flow<ResultStatus<User>> {
        return getFlowResult {
            networkDataSource.getUser(
                userId = userId
            ).toModel()
        }

    }
}