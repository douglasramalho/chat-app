package com.example.chatapp.ui.feature.signin

sealed class SignInUiEvent {
    data class UsernameChanged(val value: String) : SignInUiEvent()
    data class PasswordChanged(val value: String) : SignInUiEvent()
    object SignIn : SignInUiEvent()
}
