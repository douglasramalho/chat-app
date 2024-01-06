package com.example.chatapp.ui.feature.signup

import androidx.annotation.StringRes

data class SignUpUiState(
    val profilePhotoPath: String? = null,
    val firstName: String = "",
    @StringRes
    val firstNameError: Int? = null,
    val lastName: String = "",
    @StringRes
    val lastNameError: Int? = null,
    val email: String = "",
    @StringRes
    val emailError: Int? = null,
    val password: String = "",
    val passwordConfirmation: String = "",
    @StringRes
    val passwordError: Int? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessageStringResId: Int? = null,
)
