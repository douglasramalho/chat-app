package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatapp.ui.ChatAppState
import com.example.chatapp.ui.ChatSocketViewModel
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
                    navController.navigate("conversationGraph") {
                        popUpTo("signIn") {
                            inclusive = true
                        }
                    }
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
                    navController.navigate("conversationGraph") {
                        popUpTo("signUp") {
                            inclusive = true
                        }
                    }
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

        conversationNavGraph(
            navController = navController
        )
    }
}