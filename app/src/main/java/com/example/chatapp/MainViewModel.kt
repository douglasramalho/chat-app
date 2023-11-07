package com.example.chatapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.model.Result
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
            val result = authRepository.authenticate()
            _uiState.value = when (result) {
                Result.Loading -> UiState.Loading
                is Result.Success -> UiState.Success
                is Result.Error -> UiState.Error
            }
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data object Success : UiState
        data object Error : UiState
    }
}