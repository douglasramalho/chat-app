package com.example.chatapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApplication : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        createNotificationChannel()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        onAppForeground = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        onAppForeground = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager = NotificationManagerCompat.from(this)
            // OR getSystemService(Context.NOTIFICATION_SERVICE) as NotificationService

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "chat_messages"
        const val CHANNEL_NAME = "Messages"
        var onAppForeground: Boolean = false
            private set
    }
}