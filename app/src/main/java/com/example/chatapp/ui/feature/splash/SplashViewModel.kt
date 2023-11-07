package com.example.chatapp.ui.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _navigateAfterCheckingAuthentication = Channel<Boolean>()
    val navigateAfterCheckingAuthentication = _navigateAfterCheckingAuthentication.receiveAsFlow()

    init {
        authenticate()
    }

    private fun authenticate() {
        viewModelScope.launch {
            userRepository.currentUser.collectLatest {
                _navigateAfterCheckingAuthentication.send(it != null)
            }
        }
    }
}