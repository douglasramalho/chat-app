package com.example.chatapp.ui.feature.signin

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
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var state by mutableStateOf(SignInState())

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    init {
        authenticate()
    }

    fun onEvent(event: SignInUiEvent) {
        when (event) {
            is SignInUiEvent.UsernameChanged -> {
                state = state.copy(username = event.value)
            }

            is SignInUiEvent.PasswordChanged -> {
                state = state.copy(password = event.value)
            }

            SignInUiEvent.SignIn -> {
                signIn()
            }
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = authRepository.signIn(
                username = state.username,
                password = state.password
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