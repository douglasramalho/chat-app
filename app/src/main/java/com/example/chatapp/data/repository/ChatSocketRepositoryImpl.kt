package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.data.ChatSocketService
import com.example.chatapp.data.SocketSessionResult
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.data.repository.extension.getUserIdFromToken
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
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
    private val localDataSource: LocalDataSource,
    private val sharedPreferences: SharedPreferences,
) : ChatSocketRepository {

    override val conversationsListFlow: Flow<List<Conversation>>
        get() = localDataSource.conversationsListFlow

    override val messagesFlow: MutableStateFlow<Message?>
        get() = MutableStateFlow(null)

    override suspend fun openSession(openSocketCallback: (isOpen: Boolean) -> Unit): Flow<SocketResult> {
        val accessToken = sharedPreferences.getString("accessToken", null)
        val userId = accessToken.getUserIdFromToken()

        val socketResult = chatSocketService.initSession(userId)
        return if (socketResult.isSuccess) {
            openSocketCallback(true)
            chatSocketService.observeSocketResultFlow()
                .map {
                    when (it) {
                        is SocketSessionResult.MessageReceived ->
                            SocketResult.NewMessage(it.message.toModel(userId))

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
        } else {
            openSocketCallback(false)
            emptyFlow()
        }
    }

    override suspend fun closeSession() {
        chatSocketService.closeSession()
    }

    override suspend fun getConversationsList() {
        val accessToken = sharedPreferences.getString("accessToken", null)
        val userId = accessToken.getUserIdFromToken()
        chatSocketService.sendGetConversationsList(userId)
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

    override suspend fun getConversationBy(conversationId: String): Flow<Conversation?> {
        return conversationsListFlow.map { conversations ->
            conversations.firstOrNull { it.id == conversationId }
        }
    }
}