package com.example.chatapp.data.repository

import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.datastore.DataStoreStorage
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.data.util.getFlowResult
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val dataStoreStorage: DataStoreStorage,
) : MessageRepository {

    override suspend fun getMessages(receiverId: String): Flow<ResultStatus<List<Message>>> {
        return dataStoreStorage.currentUser.firstOrNull()?.id?.let { currentUserId ->
            getFlowResult {
                remoteDataSource.getMessages(receiverId)
                    .messages
                    .map {
                        it.toModel(currentUserId)
                    }
            }
        } ?: flowOf(ResultStatus.Error(Throwable("")))
    }
}