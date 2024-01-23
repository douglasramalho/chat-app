package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.DataStoreProtoDataSource
import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.response.toModel
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val dataStoreProtoDataSource: DataStoreProtoDataSource,
) : MessageRepository {

    private val _messagesFlow = MutableSharedFlow<List<Message>>(replay = 0)
    override val messagesFlow: Flow<List<Message>>
        get() = _messagesFlow

    override suspend fun getAndStoreMessages(receiverId: String) {
        dataStoreProtoDataSource.currentUser.firstOrNull()?.id?.let { currentUserId ->
            val messages = networkDataSource.getMessages(receiverId)
                .messages
                .map {
                    it.toModel(currentUserId)
                }

            _messagesFlow.emit(messages)
        }
    }
}