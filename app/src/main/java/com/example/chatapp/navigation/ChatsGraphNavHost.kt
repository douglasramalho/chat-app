package com.example.chatapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
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

        composable(CHATS_DESTINATION) {
            ConversationsListRoute(
                navigateWhenLogout = {
                    navController.navigate("signIn") {
                        popUpTo(CHATS_DESTINATION) {
                            inclusive = true
                        }
                    }
                },
                navigateWhenConversationItemClicked = { receiverId ->
                    navController.navigate("conversation/$receiverId")
                }
            )
        }
        composable("conversation/{receiverId}") {
            ConversationRoute {
                navController.popBackStack()
            }
        }
    }
}