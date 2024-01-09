package com.example.chatapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.domain.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        authenticate()

        viewModelScope.launch {
            authRepository.authenticateStatusFlow.collect { resultStatus ->
                _uiState.value = when (resultStatus) {
                    is ResultStatus.Error -> UiState.Error
                    ResultStatus.Loading -> UiState.Loading
                    is ResultStatus.Success -> UiState.Success
                }
            }
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            getUserInfoUseCase().collect()
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data object Success : UiState
        data object Error : UiState
    }
}