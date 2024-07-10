package com.example.chatapp.domain

import com.example.chatapp.data.dispatcher.AppDispatchers
import com.example.chatapp.data.dispatcher.Dispatcher
import com.example.chatapp.data.repository.NotificationRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RegisterPushNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    @Dispatcher(AppDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke() {
        withContext(dispatcher) {
            val token = Firebase.messaging.token.await()
            notificationRepository.registerPushNotifications(token)
        }
    }
}