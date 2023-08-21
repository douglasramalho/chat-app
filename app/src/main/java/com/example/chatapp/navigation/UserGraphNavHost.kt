package com.example.chatapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.ui.feature.userslist.UsersListRoute

fun NavGraphBuilder.userNavGraph(
    navController: NavHostController
) {

    navigation(
        route = "userGraph",
        startDestination = "usersList"
    ) {

        composable("usersList") {
            UsersListRoute(
                navigateWhenUserItemClicked = { receiverId ->
                    navController.navigate("conversation/$receiverId") {
                        popUpTo("usersList") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}