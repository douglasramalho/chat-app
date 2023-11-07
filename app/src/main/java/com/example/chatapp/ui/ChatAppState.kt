package com.example.chatapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.chatapp.model.User
import com.example.chatapp.navigation.CHATS_DESTINATION
import com.example.chatapp.navigation.PROFILE_DESTINATION
import com.example.chatapp.navigation.TopLevelDestination
import com.example.chatapp.navigation.navigateToChats
import com.example.chatapp.navigation.navigateToProfile
import com.example.chatapp.navigation.navigateToUsers

@Composable
fun rememberChatAppState(
    navController: NavHostController = rememberNavController(),
    user: User? = null,
): ChatAppState {
    return remember(navController, user) {
        ChatAppState(navController, user)
    }
}

@Stable
class ChatAppState(
    val navController: NavHostController,
    val user: User?,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            CHATS_DESTINATION -> TopLevelDestination.CHATS
            PROFILE_DESTINATION -> TopLevelDestination.PROFILE
            else -> null
        }

    val topLevelDestinations = TopLevelDestination.values().asList()

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.CHATS -> navController.navigateToChats(topLevelNavOptions)
            TopLevelDestination.CALLS -> {
            }

            TopLevelDestination.PLUS_BUTTON -> {
                navController.navigateToUsers()
            }

            TopLevelDestination.SEARCH -> {
            }

            TopLevelDestination.PROFILE -> navController.navigateToProfile()
        }
    }
}