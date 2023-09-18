package com.example.chatapp.domain

import android.util.Patterns
import com.example.chatapp.R
import com.example.chatapp.model.ValidationResult
import javax.inject.Inject

class ValidateEmailFieldUseCase @Inject constructor(
    private val validateEmptyFieldUseCase: ValidateEmptyFieldUseCase
) {

    operator fun invoke(input: String): ValidationResult {
        val validateEmptyFieldResult = validateEmptyFieldUseCase(input)
        return when {
            !validateEmptyFieldResult.successful -> validateEmptyFieldResult

            !input.isEmailValid() -> ValidationResult(
                successful = false,
                errorMessage = R.string.error_message_email_invalid
            )

            else -> ValidationResult(true)
        }
    }

    private fun String.isEmailValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}