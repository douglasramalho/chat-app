package com.example.chatapp.ui.feature.signup

sealed class SignUpUiEvent {
    data class UsernameChanged(val value: String) : SignUpUiEvent()
    data class PasswordChanged(val value: String) : SignUpUiEvent()
    data class FirstNameChanged(val value: String) : SignUpUiEvent()
    data class LastNameChanged(val value: String) : SignUpUiEvent()
    data class ProfilePictureUrlChanged(val value: String?) : SignUpUiEvent()
    object SignUp : SignUpUiEvent()
}
