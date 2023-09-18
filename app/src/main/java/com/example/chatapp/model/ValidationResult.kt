package com.example.chatapp.model

import androidx.annotation.StringRes

data class ValidationResult(
    val successful: Boolean,
    @StringRes val errorMessage: Int? = null
)
