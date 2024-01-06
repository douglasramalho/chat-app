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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.chatapp.navigation.ChatNavHost
import com.example.chatapp.navigation.TopLevelDestination
import com.example.chatapp.ui.component.ChatNavigationBar
import com.example.chatapp.ui.component.ChatNavigationRail
import com.example.chatapp.ui.theme.Grey1

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatApp(
    windowSizeClass: WindowSizeClass,
    appState: ChatAppState = rememberChatAppState(
        windowSizeClass = windowSizeClass
    )
) {
    Scaffold(
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                val topLevelDestinations = TopLevelDestination.entries.toTypedArray()
                if (topLevelDestinations.contains(appState.currentTopLevelDestination)) {
                    ChatNavigationBar(
                        destinations = appState.topLevelDestinations,
                        currentDestination = appState.currentDestination,
                        onDestinationSelected = appState::navigateToTopLevelDestination,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }
        },
        containerColor = Grey1
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            if (appState.shouldShowNavRail) {
                val topLevelDestinations = TopLevelDestination.entries.toTypedArray()
                if (topLevelDestinations.contains(appState.currentTopLevelDestination)) {
                    ChatNavigationRail(
                        destinations = appState.topLevelDestinations,
                        currentDestination = appState.currentDestination,
                        onDestinationSelected = appState::navigateToTopLevelDestination,
                        modifier = Modifier.padding(end = 1.dp)
                    )
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                ChatNavHost(appState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun PreviewChatApp() {
    ChatApp(WindowSizeClass.calculateFromSize(DpSize(300.dp, 800.dp)))
}