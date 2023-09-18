package com.example.chatapp.ui.feature.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.domain.ValidateEmailFieldUseCase
import com.example.chatapp.domain.ValidateEmptyFieldUseCase
import com.example.chatapp.model.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    init {
        authenticate()
    }

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
            formState = formState.copy(isLoading = true)
            val result = authRepository.signIn(
                username = formState.email,
                password = formState.password
            )
            resultChannel.send(result)

            formState = formState.copy(isLoading = false)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            formState = formState.copy(isLoading = true)
            val result = authRepository.authenticate()
            resultChannel.send(result)
            formState = formState.copy(isLoading = false)
        }
    }
}