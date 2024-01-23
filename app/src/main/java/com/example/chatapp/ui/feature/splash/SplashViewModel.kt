package com.example.chatapp.ui.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.domain.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : ViewModel() {

    private val _navigateAfterCheckingAuthentication = Channel<Boolean>()

    init {
        authenticate()
    }

    private fun authenticate() {
        viewModelScope.launch {
            getUserInfoUseCase().collect { resultStatus ->
                when (resultStatus) {
                    is ResultStatus.Error -> _navigateAfterCheckingAuthentication.send(false)
                    ResultStatus.Loading -> {
                    }
                    is ResultStatus.Success -> _navigateAfterCheckingAuthentication.send(true)
                }
            }
        }
    }
}