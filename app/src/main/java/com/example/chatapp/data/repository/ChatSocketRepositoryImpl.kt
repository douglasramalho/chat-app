package com.example.chatapp.data.repository

import com.example.chatapp.data.datastore.AppPreferencesDataSource
import com.example.chatapp.data.remote.request.MessageRequest
import com.example.chatapp.data.remote.request.MarkMessageAsReadRequest
import com.example.chatapp.data.remote.response.toModel
import com.example.chatapp.data.remote.ws.ChatSocketService
import com.example.chatapp.data.remote.ws.SocketSessionResult
import com.example.chatapp.data.remote.ws.WebSocketData
import com.example.chatapp.data.remote.ws.WebSocketDataType
import com.example.chatapp.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

sealed interface SocketResult {
    data class NewMessage(val message: Message) : SocketResult
    data class ActiveStatus(val activeUserIds: List<Int>) : SocketResult
    data object Empty : SocketResult
    data object Error : SocketResult
}

class ChatSocketRepositoryImpl @Inject constructor(
    private val chatSocketService: ChatSocketService,
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ChatSocketRepository {

    override val messagesFlow: MutableStateFlow<Message?>
        get() = MutableStateFlow(null)

    override suspend fun openSession() {
        val currentUser = appPreferencesDataSource.currentUser.first()
        chatSocketService.openSession(currentUser.id)
    }

    override suspend fun observeSocketResult(): Flow<SocketResult> {
        val currentUser = appPreferencesDataSource.currentUser.first()

        return chatSocketService.observeSocketResultFlow()
            .map {
                when (it) {
                    is SocketSessionResult.MessageReceived ->
                        SocketResult.NewMessage(it.message.toModel(currentUser.id))

                    is SocketSessionResult.ActiveStatus ->
                        SocketResult.ActiveStatus(it.activeUserIdsResponse.activeUserIds)

                    SocketSessionResult.EmptyResult -> SocketResult.Empty
                }
            }
    }

    override suspend fun closeSession() {
        chatSocketService.disconnect()
    }

    override suspend fun sendMessage(
        receiverId: String,
        text: String,
        timestamp: Long,
    ) {
        val messageRequest = MessageRequest(
            receiverId = receiverId,
            text = text,
            timestamp = timestamp,
        )

        val webSocketData = WebSocketData(
            type = WebSocketDataType.MESSAGE_REQUEST.value,
            data = messageRequest
        )

        chatSocketService.sendJsonStringData(Json.encodeToString(webSocketData))
    }

    override suspend fun sendReadMessage(messageId: Int) {
        val markMessageAsRead = MarkMessageAsReadRequest(
            messageId = messageId,
        )

        val webSocketData = WebSocketData(
            type = WebSocketDataType.MARK_MESSAGE_AS_READ_REQUEST.value,
            data = markMessageAsRead
        )

        chatSocketService.sendJsonStringData(Json.encodeToString(webSocketData))
    }
}