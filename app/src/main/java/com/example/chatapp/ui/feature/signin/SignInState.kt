package com.example.chatapp.ui.feature.signin

data class SignInState(
    val isLoading: Boolean = false,
    val email: String = "",
    val emailError: Int? = null,
    val password: String = "",
    val passwordError: Int? = null,
)
