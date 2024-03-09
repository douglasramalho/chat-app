package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.example.chatapp.ui.ChatAppState
import com.example.chatapp.ui.feature.signin.SignInRoute
import com.example.chatapp.ui.feature.signup.SignUpRoute
import com.example.chatapp.ui.feature.splash.SplashRoute

const val SPLASH_ROUTE = "splash_route"
const val SIGN_IN_ROUTE = "signIn"
const val SIGN_UP_ROUTE = "signUp"

@Composable
fun ChatNavHost(
    appState: ChatAppState,
    closeApp: () -> Unit,
) {
    val navController = appState.navController

    NavHost(navController = appState.navController, startDestination = SPLASH_ROUTE) {
        composable(SPLASH_ROUTE) {
            val navOptions = navOptions {
                popUpTo(SPLASH_ROUTE) {
                    inclusive = true
                }
            }

            SplashRoute(
                navigateToConversationsList = {
                    navController.navigateToChats(navOptions)
                },
                navigateToSigIn = {
                    navController.navigate(SIGN_IN_ROUTE, navOptions)
                },
                closeApp = closeApp
            )
        }
        composable(SIGN_IN_ROUTE) {
            SignInRoute(
                navigateWhenAuthorized = {
                    val navOptions = navOptions {
                        popUpTo(SIGN_IN_ROUTE) {
                            inclusive = true
                        }
                    }
                    navController.navigateToChats(navOptions)
                },
                navigateWhenSignUpClicked = {
                    navController.navigate(SIGN_UP_ROUTE) {
                        popUpTo(SIGN_UP_ROUTE) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(SIGN_UP_ROUTE) {
            SignUpRoute(
                navigateWhenAuthorized = {
                    val navOptions = navOptions {
                        popUpTo(SIGN_UP_ROUTE) {
                            inclusive = true
                        }
                    }
                    navController.navigateToChats(navOptions)
                },
                navigateWhenSigInClicked = {
                    navController.navigate(SIGN_IN_ROUTE) {
                        popUpTo(SIGN_IN_ROUTE) {
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

        profileNavGraph(navController = navController)
    }
}