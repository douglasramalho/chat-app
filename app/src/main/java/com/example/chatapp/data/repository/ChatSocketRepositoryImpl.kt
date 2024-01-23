package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.DataStoreProtoDataSource
import com.example.chatapp.data.network.response.toModel
import com.example.chatapp.data.ws.ChatSocketService
import com.example.chatapp.data.ws.SocketSessionResult
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

sealed interface SocketResult {
    data class NewMessage(val message: Message) : SocketResult
    data class UnreadStatus(
        val hasConversationsUnread: Boolean,
        val unreadMessagesCount: Int
    ) : SocketResult

    data class ActiveStatus(val activeUserIds: List<Int>) : SocketResult
    data object Empty : SocketResult
    data object Error : SocketResult
}

class ChatSocketRepositoryImpl @Inject constructor(
    private val chatSocketService: ChatSocketService,
    private val dataStoreProtoDataSource: DataStoreProtoDataSource,
) : ChatSocketRepository {

    override val messagesFlow: MutableStateFlow<Message?>
        get() = MutableStateFlow(null)

    override suspend fun openSession() {
        val currentUser = dataStoreProtoDataSource.currentUser.first()
        chatSocketService.openSession(currentUser.id)
    }

    override suspend fun observeSocketResult(): Flow<SocketResult> {
        val currentUser = dataStoreProtoDataSource.currentUser.first()

        return chatSocketService.observeSocketResultFlow()
            .map {
                when (it) {
                    is SocketSessionResult.MessageReceived ->
                        SocketResult.NewMessage(it.message.toModel(currentUser.id))

                    is SocketSessionResult.UnreadStatus -> {
                        SocketResult.UnreadStatus(
                            hasConversationsUnread = it.unreadStatusResponse.hasConversationsUnread,
                            unreadMessagesCount = it.unreadStatusResponse.unreadMessagesCount,
                        )
                    }

                    is SocketSessionResult.ActiveStatus -> SocketResult.ActiveStatus(it.activeStatusResponse.activeUserIds)

                    SocketSessionResult.EmptyResult -> SocketResult.Empty
                }
            }
    }

    override suspend fun closeSession() {
        chatSocketService.closeSession()
    }

    override suspend fun getOnlineStatus() {
        chatSocketService.sendGetActiveStatus()
    }

    override suspend fun sendMessage(
        receiverId: String,
        message: String
    ) {
        chatSocketService.sendMessage(receiverId, message)
    }

    override suspend fun sendReadMessage(messageId: Int) {
        chatSocketService.sendReadMessage(messageId)
    }
}