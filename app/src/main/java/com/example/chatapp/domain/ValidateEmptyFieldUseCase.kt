package com.example.chatapp.domain

import com.example.chatapp.R
import com.example.chatapp.model.ValidationResult
import javax.inject.Inject

class ValidateEmptyFieldUseCase @Inject constructor() {

    operator fun invoke(input: String): ValidationResult {
        return when {
            input.isBlank() -> ValidationResult(
                successful = false,
                errorMessage = R.string.error_message_field_blank
            )

            else -> ValidationResult(true)
        }
    }
}