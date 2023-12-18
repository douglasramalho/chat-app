package com.example.chatapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.ui.ChatSocketViewModel
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
        composable(CHATS_DESTINATION) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(CHATS_ROUTE)
            }

            val chatSocketViewModel: ChatSocketViewModel = hiltViewModel(parentEntry)

            ConversationsListRoute(
                chatSocketViewModel = chatSocketViewModel,
                navigateWhenConversationItemClicked = { receiverId ->
                    navController.navigate("conversation/$receiverId")
                }
            )
        }
        composable("conversation/{receiverId}",
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
            val parentEntry = remember(it) {
                navController.getBackStackEntry(CHATS_ROUTE)
            }

            val chatSocketViewModel: ChatSocketViewModel = hiltViewModel(parentEntry)
            chatSocketViewModel.setReceiverId(it.arguments?.getString("receiverId"))

            ConversationRoute(
                chatSocketViewModel = chatSocketViewModel,
                receiverId = it.arguments?.getString("receiverId")
            ) {
                navController.popBackStack()
            }
        }
    }
}