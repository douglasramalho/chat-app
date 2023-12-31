package com.example.chatapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.data.util.ResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        authenticate()
    }

    private fun authenticate() {
        viewModelScope.launch {
            authRepository.authenticate().collect { resultStatus ->
                _uiState.value = when (resultStatus) {
                    is ResultStatus.Error -> UiState.Error
                    ResultStatus.Loading -> UiState.Loading
                    is ResultStatus.Success -> UiState.Success
                }
            }
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data object Success : UiState
        data object Error : UiState
    }
}