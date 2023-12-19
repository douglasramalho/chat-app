package com.example.chatapp.ui.feature.userlist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.data.util.ResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = mutableStateOf(UserListState())
    val state: State<UserListState> = _state

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )

            userRepository.getUsers().collect { resultStatus ->
                when (resultStatus) {
                    is ResultStatus.Error -> {

                    }
                    ResultStatus.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                        )
                    }
                    is ResultStatus.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            usersList = resultStatus.data,
                        )
                    }
                }
            }
        }
    }
}