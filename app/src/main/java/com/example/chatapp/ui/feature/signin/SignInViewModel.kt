package com.example.chatapp.ui.feature.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.domain.ValidateEmailFieldUseCase
import com.example.chatapp.domain.ValidateEmptyFieldUseCase
import com.example.chatapp.model.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val validateEmptyFieldUseCase: ValidateEmptyFieldUseCase,
    private val validateEmailFieldUseCase: ValidateEmailFieldUseCase,
) : ViewModel() {

    var formState by mutableStateOf(SignInState())

    private val _signInResultUiState =
        MutableStateFlow<SignInResultUiState>(SignInResultUiState.Success)
    val signInResultUiState = _signInResultUiState.asStateFlow()

    private val _navigateWhenSigningSuccessfully = Channel<Unit>()
    val navigateWhenSigningSuccessfully = _navigateWhenSigningSuccessfully.receiveAsFlow()

    fun onEvent(event: SignInUiEvent) {
        when (event) {
            is SignInUiEvent.UsernameChanged -> {
                formState = formState.copy(email = event.value)
            }

            is SignInUiEvent.PasswordChanged -> {
                formState = formState.copy(password = event.value)
            }

            SignInUiEvent.SignIn -> {
                if (isFormValid()) {
                    signIn()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        val emailValidationResult = validateEmailFieldUseCase(formState.email)
        formState = formState.copy(emailError = emailValidationResult.errorMessage)

        val passwordValidationResult = validateEmptyFieldUseCase(formState.password)
        formState = formState.copy(passwordError = passwordValidationResult.errorMessage)

        return listOf(
            emailValidationResult.successful,
            passwordValidationResult.successful,
        ).all { it }
    }

    private fun signIn() {
        viewModelScope.launch {
            authRepository.signIn(
                username = formState.email,
                password = formState.password
            ).collect { resultStatus ->
                _signInResultUiState.value = when (resultStatus) {
                    ResultStatus.Loading -> SignInResultUiState.Loading
                    is ResultStatus.Success -> {
                        _navigateWhenSigningSuccessfully.send(Unit)
                        SignInResultUiState.Success
                    }

                    is ResultStatus.Error -> when (resultStatus.exception) {
                        AppError.ApiError.Conflict -> SignInResultUiState.Error.InvalidUsernameOrPassword
                        else -> SignInResultUiState.Error.Generic
                    }
                }
            }
        }
    }

    fun resetSignInResultUiState() {
        _signInResultUiState.value = SignInResultUiState.Success
    }

    sealed interface SignInResultUiState {
        data object Loading : SignInResultUiState
        sealed interface Error : SignInResultUiState {
            data object InvalidUsernameOrPassword : Error
            data object Generic : Error
        }

        data object Success : SignInResultUiState
    }
}