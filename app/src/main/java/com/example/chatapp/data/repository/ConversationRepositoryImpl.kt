package com.example.chatapp.data.repository

import com.example.chatapp.data.RemoteDataSource
import com.example.chatapp.data.datastore.DataStoreStorage
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.data.util.getFlowResult
import com.example.chatapp.model.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val dataStoreStorage: DataStoreStorage,
) : ConversationRepository {

    override suspend fun getConversations(): Flow<ResultStatus<List<Conversation>>> {
        return dataStoreStorage.currentUser.firstOrNull()?.id?.let { currentUserId ->
            getFlowResult {
                val response = remoteDataSource.getConversations()
                response.conversations.map { it.toModel(currentUserId) }
            }
        } ?: flowOf(ResultStatus.Error(Throwable("Error when trying to retrieve the current user information")))
    }
}