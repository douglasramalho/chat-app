package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.AppPreferencesDataSource
import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.response.toModel
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.data.util.getFlowResult
import com.example.chatapp.model.Image
import com.example.chatapp.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    appPreferencesDataSource: AppPreferencesDataSource,
    private val networkDataSource: NetworkDataSource,
) : UserRepository {

    override val currentUser: Flow<User> = appPreferencesDataSource.currentUser
        .map {
            User(
                id = it.id,
                username = it.email,
                firstName = it.firstName,
                lastName = it.firstName,
                profilePictureUrl = it.profilePictureUrl,
            )
        }

    override suspend fun uploadProfilePicture(filePath: String): Image {
        return networkDataSource.uploadProfilePicture(filePath).toModel()
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