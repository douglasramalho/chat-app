package com.example.chatapp.ui.feature.signin

data class SignInState(
    val isLoading: Boolean = false,
    val username: String = "",
    val password: String = "",
)
