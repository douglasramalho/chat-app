package com.example.chatapp.data.repository

import android.content.SharedPreferences
import com.example.chatapp.data.ChatSocketService
import com.example.chatapp.data.SocketSessionResult
import com.example.chatapp.data.remote.response.toConversation
import com.example.chatapp.data.remote.response.toMessage
import com.example.chatapp.data.repository.extension.getUserIdFromToken
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface SocketResult {
    object Open : SocketResult
    data class NewMessage(val message: Message) : SocketResult
    data class Conversations(val conversations: List<Conversation>) : SocketResult
    data class OnlineStatus(val onlineUserIds: List<Int>) : SocketResult
}

class ChatSocketRepositoryImpl @Inject constructor(
    private val chatSocketService: ChatSocketService,
    private val localDataSource: LocalDataSource,
    private val sharedPreferences: SharedPreferences,
) : ChatSocketRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override val conversationsListFlow: Flow<List<Conversation>>
        get() = localDataSource.conversationsListFlow

    override suspend fun openSession(): Flow<SocketResult?> {
        val accessToken = sharedPreferences.getString("accessToken", null)
        val userId = accessToken.getUserIdFromToken()
        return withContext(coroutineScope.coroutineContext) {
            chatSocketService.initSession(userId).map {
                when (it) {
                    is SocketSessionResult.SocketOpen -> SocketResult.Open
                    is SocketSessionResult.MessageReceived -> {
                        val message = it.message.toMessage(userId)
                        SocketResult.NewMessage(message)
                    }

                    is SocketSessionResult.ConversationsList -> {
                        val conversations = it.conversationsList.map { response ->
                            response.toConversation(userId)
                        }

                        localDataSource.saveConversationsList(conversations)

                        SocketResult.Conversations(localDataSource.getConversationsList())
                    }

                    is SocketSessionResult.OnlineStatus -> {
                        SocketResult.OnlineStatus(it.onlineStatusResponse.onlineUserIds)
                    }

                    else -> null
                }
            }
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
        chatSocketService.sendGetOnlineStatus()
    }

    override suspend fun sendMessage(
        receiverId: String,
        message: String
    ) {
        chatSocketService.sendMessage(receiverId, message)
    }

    override suspend fun sendReadMessage(messageId: String) {
        chatSocketService.sendReadMessage(messageId)
    }

    override suspend fun getConversationBy(conversationId: String): Flow<Conversation?> {
        return conversationsListFlow.map { conversations ->
            conversations.firstOrNull { it.id == conversationId }
        }
    }
}