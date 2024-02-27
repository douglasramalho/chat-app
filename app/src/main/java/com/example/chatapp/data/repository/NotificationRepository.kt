package com.example.chatapp.data.repository

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    val reloadConversationsListFlow: Flow<Unit?>

    suspend fun setNotificationAction(action: String)

    suspend fun registerPushNotifications(newToken: String): String
}