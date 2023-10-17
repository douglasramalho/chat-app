package com.example.chatapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.ui.feature.profile.ProfileRoute

const val PROFILE_ROUTE = "profileGraph"
const val PROFILE_DESTINATION = "profile"

fun NavController.navigateToProfile() {
    this.navigate(PROFILE_ROUTE)
}


fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController
) {
    navigation(
        route = PROFILE_ROUTE,
        startDestination = PROFILE_DESTINATION
    ) {

        composable(PROFILE_DESTINATION) {
            ProfileRoute(
                navigateWhenLogout = {
                    navController.navigate("signIn") {
                        popUpTo(CHATS_DESTINATION) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}