package com.example.chatapp.ui.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.domain.GetUserInfoUseCase
import com.example.chatapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        authenticate()
    }

    private fun authenticate() {
        viewModelScope.launch {
            getUserInfoUseCase().collect { resultStatus ->
                _uiState.value = when (resultStatus) {
                    is ResultStatus.Error -> SplashUiState.Error
                    ResultStatus.Loading -> SplashUiState.Loading
                    is ResultStatus.Success -> {
                        val user = userRepository.currentUser.first()
                        SplashUiState.Success(user)
                    }
                }
            }
        }
    }

    sealed interface SplashUiState {
        data object Loading : SplashUiState
        data class Success(val user: User) : SplashUiState
        data object Error : SplashUiState
    }
}