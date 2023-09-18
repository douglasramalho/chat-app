package com.example.chatapp.domain

import com.example.chatapp.R
import com.example.chatapp.model.ValidationResult
import javax.inject.Inject

class ValidatePasswordFieldUseCase @Inject constructor() {

    operator fun invoke(input: String, inputConfirmation: String): ValidationResult {
        return when {
            input.isLessThanEightChars() -> ValidationResult(
                successful = false,
                errorMessage = R.string.error_message_password_less_than_eight_chars
            )

            !input.isPasswordValid() -> ValidationResult(
                successful = false,
                errorMessage = R.string.error_message_password_invalid
            )

            input != inputConfirmation -> ValidationResult(
                successful = false,
                errorMessage = R.string.error_message_password_confirmation_invalid
            )

            else -> ValidationResult(true)
        }
    }

    private fun String.isLessThanEightChars(): Boolean {
        return this.length < 6
        8
    }

    private fun String.isPasswordValid(): Boolean {
        return this.any { it.isDigit() } &&
                this.any { it.isLetter() }
    }
}