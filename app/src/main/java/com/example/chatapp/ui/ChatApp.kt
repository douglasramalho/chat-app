package com.example.chatapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.navigation.ChatNavHost
import com.example.chatapp.navigation.TopLevelDestination
import com.example.chatapp.ui.component.ChatNavigationBar
import com.example.chatapp.ui.theme.Grey1

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatApp(
    appState: ChatAppState = rememberChatAppState()
) {
    Scaffold(
        bottomBar = {
            val topLevelDestinations = TopLevelDestination.values()
            if (topLevelDestinations.contains(appState.currentTopLevelDestination)) {
                ChatNavigationBar(
                    destinations = appState.topLevelDestinations,
                    currentDestination = appState.currentDestination,
                    onDestinationSelected = appState::navigateToTopLevelDestination,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        },
        containerColor = Grey1
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ChatNavHost(appState)
            }
        }
    }
}

@Preview
@Composable
fun PreviewChatApp() {
    ChatApp()
}