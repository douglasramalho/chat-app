package com.example.chatapp.service

import android.Manifest
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.chatapp.ChatApplication
import com.example.chatapp.R
import com.example.chatapp.data.di.ApplicationScope
import com.example.chatapp.data.repository.NotificationRepository
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.data.util.NotificationParse
import com.example.chatapp.model.MessageNotificationPayload
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FcmMessagingService @Inject constructor() : FirebaseMessagingService() {

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var notificationParse: NotificationParse

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send new token to the backend
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        applicationScope.launch {
            val currentUser = userRepository.currentUser.firstOrNull()
            if (currentUser?.id.isNullOrEmpty()) {
                return@launch
            }

            val payloadDtoStr = message.data["messagePayload"]
            notificationParse.parseMessagePayload(payloadDtoStr)
                ?.let { messageNotificationPayload ->
                    if (ChatApplication.onAppForeground) {
                        applicationScope.launch {
                            notificationRepository.setNotificationAction(messageNotificationPayload.action)
                        }

                        return@launch
                    }

                    sendNotification(messageNotificationPayload)
                }
        }
    }

    private suspend fun sendNotification(messageNotificationPayload: MessageNotificationPayload) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent().apply {
                action = Intent.ACTION_VIEW
                data =
                    Uri.parse("https://chatapp.androidmoderno.com.br/conversation/${messageNotificationPayload.userId}")
                component = ComponentName(
                    packageName,
                    "com.example.chatapp.MainActivity",
                )
            },
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE,
        )

        val notificationManager = NotificationManagerCompat.from(this)
        val channelId = ChatApplication.CHANNEL_ID

        val loader = ImageLoader(this@FcmMessagingService)
        val request = ImageRequest.Builder(this@FcmMessagingService)
            .data(messageNotificationPayload.profilePictureUrl)
            .build()

        val result = (loader.execute(request) as SuccessResult).drawable
        val bitmap = (result as BitmapDrawable).bitmap

        val notificationBuilder = NotificationCompat.Builder(this@FcmMessagingService, channelId)
            .setContentTitle(messageNotificationPayload.userName)
            .setContentText(messageNotificationPayload.message)
            .setLargeIcon(bitmap)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(messageNotificationPayload.userId, notificationBuilder.build())
    }
}