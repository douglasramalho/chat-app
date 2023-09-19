package com.example.chatapp.ui.feature.signin

data class SignInState(
    val email: String = "",
    val emailError: Int? = null,
    val password: String = "",
    val passwordError: Int? = null,
)
