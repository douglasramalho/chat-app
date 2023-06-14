package com.example.chatapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.navigation.TopLevelDestination

@Composable
fun rememberChatAppState(
    navController: NavHostController = rememberNavController()
): ChatAppState {
    return remember(navController) {
        ChatAppState(navController)
    }
}

@Stable
class ChatAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            "conversationsList" -> TopLevelDestination.CONVERSATIONS_LIST
            else -> null
        }
}