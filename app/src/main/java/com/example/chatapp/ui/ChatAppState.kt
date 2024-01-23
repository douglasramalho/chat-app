package com.example.chatapp.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.chatapp.navigation.CHATS_DESTINATION
import com.example.chatapp.navigation.PROFILE_DESTINATION
import com.example.chatapp.navigation.TopLevelDestination
import com.example.chatapp.navigation.navigateToChats
import com.example.chatapp.navigation.navigateToProfile
import com.example.chatapp.navigation.navigateToUsers

@Composable
fun rememberChatAppState(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
    startDestination: String,
): ChatAppState {
    return remember(
        navController,
        windowSizeClass,
        startDestination,
    ) {
        ChatAppState(
            windowSizeClass,
            navController,
            startDestination,
        )
    }
}

@Stable
class ChatAppState(
    val windowSizeClass: WindowSizeClass,
    val navController: NavHostController,
    val startDestination: String,
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

    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar

    val topLevelDestinations = TopLevelDestination.entries

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo("chats") {
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

            TopLevelDestination.PLUS_BUTTON -> {
                navController.navigateToUsers()
            }

            TopLevelDestination.PROFILE -> navController.navigateToProfile(topLevelNavOptions)
        }
    }
}

fun NavDestination?.isTopLevelInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false