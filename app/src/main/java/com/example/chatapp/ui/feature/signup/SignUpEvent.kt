package com.example.chatapp.ui.feature.signup

import android.net.Uri

sealed class SignUpEvent {
    data class FirstNameChanged(val value: String) : SignUpEvent()
    data class LastNameChanged(val value: String) : SignUpEvent()
    data class EmailChanged(val value: String) : SignUpEvent()
    data class PasswordChanged(val value: String) : SignUpEvent()
    data class PasswordConfirmationChanged(val value: String) : SignUpEvent()
    data class ProfilePhotoUriChanged(val value: Uri?) : SignUpEvent()
    object Submit : SignUpEvent()
}
