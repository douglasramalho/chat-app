package com.example.chatapp.data.repository

import com.example.chatapp.data.network.NetworkDataSource
import com.example.chatapp.data.network.request.RegisterPushRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
) : NotificationRepository {

    private val _reloadConversationsListFlow = MutableSharedFlow<Unit?>(replay = 0)
    override val reloadConversationsListFlow: Flow<Unit?>
        get() = _reloadConversationsListFlow

    override suspend fun setNotificationAction(action: String) {
        when (action) {
            "newMessage" -> _reloadConversationsListFlow.emit(Unit)
        }
    }

    override suspend fun registerPushNotifications(newToken: String): String {
        networkDataSource.registerNotifications(
            RegisterPushRequest(
                token = newToken
            )
        )

        return newToken
    }
}