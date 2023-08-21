package com.example.chatapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.ui.feature.conversation.ConversationRoute
import com.example.chatapp.ui.feature.conversationslist.ConversationsListRoute

fun NavGraphBuilder.conversationNavGraph(
    navController: NavHostController
) {

    navigation(
        route = "conversationGraph",
        startDestination = "conversationsList"
    ) {

        composable("conversationsList") {
            ConversationsListRoute(
                navigateWhenLogout = {
                    navController.navigate("signIn") {
                        popUpTo("conversationsList") {
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