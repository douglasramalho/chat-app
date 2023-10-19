package com.example.chatapp.ui.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _navigateAfterCheckingAuthentication = Channel<Boolean>()
    val navigateAfterCheckingAuthentication = _navigateAfterCheckingAuthentication.receiveAsFlow()

    init {
        authenticate()
    }

    private fun authenticate() {
        viewModelScope.launch {
            val result = authRepository.authenticate()

            if (result is Result.Success) {
                _navigateAfterCheckingAuthentication.send(true)
            }

            if (result is Result.Error) {
                _navigateAfterCheckingAuthentication.send(false)
            }
        }
    }
}