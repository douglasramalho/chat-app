package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.DataStoreProtoDataSource
import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.response.toModel
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.data.util.getFlowResult
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val dataStoreProtoDataSource: DataStoreProtoDataSource,
) : MessageRepository {

    override suspend fun getMessages(receiverId: String): Flow<ResultStatus<List<Message>>> {
        return dataStoreProtoDataSource.currentUser.firstOrNull()?.id?.let { currentUserId ->
            getFlowResult {
                networkDataSource.getMessages(receiverId)
                    .messages
                    .map {
                        it.toModel(currentUserId)
                    }
            }
        } ?: flowOf(ResultStatus.Error(Throwable("")))
    }
}