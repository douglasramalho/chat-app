package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.chatapp.ui.ChatApp
import com.example.chatapp.ui.ChatSocketViewModel
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ChatSocketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalActivity provides this@MainActivity) {
                ChatAppTheme {
                    ChatApp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.connectChatSocket()
    }

    override fun onStop() {
        super.onStop()
        viewModel.closeConnection()
    }
}

val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("LocalActivity is not present")
}