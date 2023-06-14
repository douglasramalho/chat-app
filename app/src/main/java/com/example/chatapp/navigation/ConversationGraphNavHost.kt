package com.example.chatapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.ui.feature.conversation.ConversationRoute
import com.example.chatapp.ui.feature.conversationlist.ConversationsListRoute

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
                navigateWhenConversationItemClicked = { conversationId ->
                    navController.navigate("conversation/$conversationId")
                }
            )
        }
        composable("conversation/{conversationId}") {
            ConversationRoute {
                navController.popBackStack()
            }
        }
    }
}