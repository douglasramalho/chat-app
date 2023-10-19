package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.example.chatapp.ui.ChatAppState
import com.example.chatapp.ui.feature.signin.SignInRoute
import com.example.chatapp.ui.feature.signup.SignUpRoute
import com.example.chatapp.ui.feature.splash.SplashRoute

@Composable
fun ChatNavHost(
    appState: ChatAppState,
    startDestination: String = "splash"
) {
    val navController = appState.navController

    NavHost(navController = appState.navController, startDestination = startDestination) {
        composable("splash") {
            SplashRoute { isLoggedIn ->
                val navOptions = navOptions {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }

                if (isLoggedIn) {
                    navController.navigateToChats(navOptions)
                } else navController.navigate("signIn")
            }
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