package com.example.chatapp.ui.feature.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.model.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var state by mutableStateOf(SignUpState())

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    init {
        // authenticate()
    }

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.UsernameChanged -> {
                state = state.copy(username = event.value)
            }

            is SignUpUiEvent.PasswordChanged -> {
                state = state.copy(password = event.value)
            }

            is SignUpUiEvent.FirstNameChanged -> {
                state = state.copy(firstName = event.value)
            }

            is SignUpUiEvent.LastNameChanged -> {
                state = state.copy(lastName = event.value)
            }

            is SignUpUiEvent.ProfilePictureUrlChanged -> {
                state = state.copy(profilePictureUrl = event.value)
            }

            SignUpUiEvent.SignUp -> {
                signUp()
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = authRepository.signUp(
                username = state.username,
                password = state.password,
                firstName = state.firstName,
                lastName = state.lastName,
                profilePictureUrl = state.profilePictureUrl
            )
            resultChannel.send(result)

            state = state.copy(isLoading = false)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = authRepository.authenticate()
            resultChannel.send(result)
            state = state.copy(isLoading = false)
        }
    }
}