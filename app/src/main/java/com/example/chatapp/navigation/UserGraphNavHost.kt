package com.example.chatapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.ui.feature.userslist.UsersListRoute

const val USERS_ROUTE = "userSGraph"
const val USERS_DESTINATION = "users"

fun NavController.navigateToUsers() {
    this.navigate(USERS_ROUTE)
}


fun NavGraphBuilder.userNavGraph(
    navController: NavHostController
) {
    navigation(
        route = USERS_ROUTE,
        startDestination = USERS_DESTINATION
    ) {

        composable(USERS_DESTINATION) {
            UsersListRoute(
                navigateWhenUserItemClicked = { receiverId ->
                    navController.navigate("conversation/$receiverId") {
                        popUpTo(USERS_DESTINATION) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}