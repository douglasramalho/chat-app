package com.example.chatapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.example.chatapp.ui.feature.conversation.ConversationRoute
import com.example.chatapp.ui.feature.conversationslist.ConversationsListRoute

const val CHATS_ROUTE = "chatsGraph"
const val CHATS_DESTINATION = "chats"

fun NavController.navigateToChats(navOptions: NavOptions? = null) {
    this.navigate(CHATS_ROUTE, navOptions)
}

fun NavGraphBuilder.chatsNavGraph(
    navController: NavHostController
) {
    navigation(
        route = CHATS_ROUTE,
        startDestination = CHATS_DESTINATION
    ) {
        composable(
            CHATS_DESTINATION,
        ) {
            ConversationsListRoute(
                navigateWhenConversationItemClicked = { receiverId ->
                    navController.navigate("conversation/$receiverId")
                }
            )
        }
        composable(
            route = "conversation/{receiverId}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://chatapp.androidmoderno.com.br/conversation/{receiverId}"
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(150)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(150)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(150)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(150)
                )
            }
        ) {
            ConversationRoute(
                receiverId = it.arguments?.getString("receiverId")
            ) {
                navController.popBackStack()
            }
        }
    }
}