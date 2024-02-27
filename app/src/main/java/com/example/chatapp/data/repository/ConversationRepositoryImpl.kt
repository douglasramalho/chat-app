package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.AppPreferencesDataSource
import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.response.toModel
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.data.util.getFlowResult
import com.example.chatapp.model.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ConversationRepository {

    override suspend fun getConversations(): Flow<ResultStatus<List<Conversation>>> {
        return appPreferencesDataSource.currentUser.firstOrNull()?.id?.let { currentUserId ->
            getFlowResult {
                val response = networkDataSource.getConversations()
                response.conversations.map { it.toModel(currentUserId) }
            }
        } ?: flowOf(ResultStatus.Error(Throwable("Error when trying to retrieve the current user information")))
    }
}