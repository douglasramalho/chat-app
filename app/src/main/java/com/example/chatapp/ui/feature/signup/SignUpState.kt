package com.example.chatapp.ui.feature.signup

data class SignUpState(
    val isLoading: Boolean = false,
    val username: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profilePictureUrl: String? = null
)
