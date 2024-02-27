package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.example.chatapp.ui.ChatAppState
import com.example.chatapp.ui.feature.signin.SignInRoute
import com.example.chatapp.ui.feature.signup.SignUpRoute
import com.example.chatapp.ui.feature.splash.SplashRoute
import kotlinx.coroutines.delay

@Composable
fun ChatNavHost(
    appState: ChatAppState,
    closeApp: () -> Unit,
) {
    val navController = appState.navController

    NavHost(navController = appState.navController, startDestination = "splash") {
        composable("splash") {
            val navOptions = navOptions {
                popUpTo("splash") {
                    inclusive = true
                }
            }

            SplashRoute(
                navigateToConversationsList = {
                    navController.navigateToChats(navOptions)
                },
                navigateToSigIn = {
                    navController.navigate("signIn", navOptions)
                },
                closeApp = closeApp
            )
        }
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

        profileNavGraph(navController = navController)
    }
}