package com.example.chatapp.ui.feature.signup

import android.net.Uri
import androidx.annotation.StringRes

data class SignUpState(
    val isLoading: Boolean = false,
    val profilePhotoUri: Uri? = null,
    val firstName: String = "",
    @StringRes val firstNameError: Int? = null,
    val lastName: String = "",
    @StringRes val lastNameError: Int? = null,
    val email: String = "",
    @StringRes val emailError: Int? = null,
    val password: String = "",
    val passwordConfirmation: String = "",
    @StringRes val passwordError: Int? = null,
)
