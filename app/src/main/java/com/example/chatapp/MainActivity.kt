package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.chatapp.ui.ChatApp
import com.example.chatapp.ui.ChatSocketViewModel
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val chatSocketViewModel: ChatSocketViewModel by viewModels()
    private var uiState: MainViewModel.UiState by mutableStateOf(MainViewModel.UiState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.onEach {
                    if (it is MainViewModel.UiState.Success) {
                        chatSocketViewModel.openSocketConnection()
                    }
                    uiState = it
                }.collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainViewModel.UiState.Loading -> true
                else -> false
            }
        }

        setContent {
            CompositionLocalProvider {
                ChatAppTheme {
                    ChatApp()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        chatSocketViewModel.closeSocketConnection()
    }
}