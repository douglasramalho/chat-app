package com.example.chatapp.domain

import com.example.chatapp.data.repository.NotificationRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterPushNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {

    suspend operator fun invoke(newToken: String? = null): String {
        val token = newToken ?: Firebase.messaging.token.await()
        return notificationRepository.registerPushNotifications(token)
    }
}