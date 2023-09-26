package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.example.chatapp.ui.ChatAppState
import com.example.chatapp.ui.feature.signin.SignInRoute
import com.example.chatapp.ui.feature.signup.SignUpRoute

@Composable
fun ChatNavHost(
    appState: ChatAppState,
    startDestination: String = "signIn"
) {
    val navController = appState.navController

    NavHost(navController = appState.navController, startDestination = startDestination) {
        composable("signIn") {
            SignInRoute(
                navigateWhenAuthorized = {
                    val navOptions = navOptions {
                        popUpTo("signIn") {
                            inclusive = true
                        }
                    }
                    navController.navigateToChats(navOptions)
                },
                navigateWhenSignUpClicked = {
                    navController.navigate("signUp") {
                        popUpTo("signIn") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("signUp") {
            SignUpRoute(
                navigateWhenAuthorized = {
                    val navOptions = navOptions {
                        popUpTo("signUp") {
                            inclusive = true
                        }
                    }
                    navController.navigateToChats(navOptions)
                },
                navigateWhenSigInClicked = {
                    navController.navigate("signIn") {
                        popUpTo("signUp") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        chatsNavGraph(
            navController = navController
        )

        userNavGraph(navController = navController)
    }
}