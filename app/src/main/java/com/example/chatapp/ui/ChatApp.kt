package com.example.chatapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.chatapp.R
import com.example.chatapp.navigation.ChatNavHost
import com.example.chatapp.navigation.TopLevelDestination

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatApp(
    appState: ChatAppState = rememberChatAppState()
) {
    Scaffold(
        floatingActionButton = {
            if (appState.currentTopLevelDestination == TopLevelDestination.CONVERSATIONS_LIST) {
                FloatingActionButton(onClick = {
                    appState.navController.navigate("userGraph")
                }) {

                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .consumedWindowInsets(paddingValues)
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