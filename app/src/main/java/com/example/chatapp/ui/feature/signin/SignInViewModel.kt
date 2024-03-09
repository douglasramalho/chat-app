package com.example.chatapp.ui.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.R
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.domain.SignInUseCase
import com.example.chatapp.domain.ValidateEmailFieldUseCase
import com.example.chatapp.domain.ValidateEmptyFieldUseCase
import com.example.chatapp.model.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val validateEmptyFieldUseCase: ValidateEmptyFieldUseCase,
    private val validateEmailFieldUseCase: ValidateEmailFieldUseCase,
) : ViewModel() {

    private val _signInUiState = MutableStateFlow(SignInUiState())
    val signInUiState = _signInUiState.asStateFlow()

    fun onEvent(event: SignInUiEvent) {
        when (event) {
            is SignInUiEvent.UsernameChanged -> {
                _signInUiState.update {
                    it.copy(email = event.value)
                }
            }

            is SignInUiEvent.PasswordChanged -> {
                _signInUiState.update {
                    it.copy(password = event.value)
                }
            }

            SignInUiEvent.SignIn -> {
                if (isFormValid()) {
                    signIn()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        val emailValidationResult = validateEmailFieldUseCase(_signInUiState.value.email)
        val passwordValidationResult = validateEmptyFieldUseCase(_signInUiState.value.password)

        _signInUiState.update {
            it.copy(
                emailError = emailValidationResult.errorMessage,
                passwordError = passwordValidationResult.errorMessage
            )
        }

        return listOf(
            emailValidationResult.successful,
            passwordValidationResult.successful,
        ).all { it }
    }

    private fun signIn() {
        viewModelScope.launch {
            signInUseCase(
                SignInUseCase.Params(
                    email = _signInUiState.value.email,
                    password = _signInUiState.value.password
                )
            ).collectLatest { resultStatus ->
                _signInUiState.update {
                    it.copy(
                        isLoading = resultStatus is ResultStatus.Loading,
                        isLoggedIn = resultStatus is ResultStatus.Success,
                        errorMessageStringResId = if (resultStatus is ResultStatus.Error) {
                            if (resultStatus.exception is AppError.ApiError.Conflict) {
                                R.string.error_message_invalid_username_or_password
                            } else R.string.common_generic_error_message
                        } else null
                    )
                }
            }
        }
    }

    fun errorMessageShown() {
        _signInUiState.update {
            it.copy(errorMessageStringResId = null)
        }
    }
}