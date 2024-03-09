package com.example.chatapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.ui.feature.userslist.UserListRoute

const val USERS_ROUTE = "usersRoute"
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

        composable(
            USERS_DESTINATION,
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
            UserListRoute(
                onNavigationClick = {
                    navController.popBackStack()
                },
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