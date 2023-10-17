package com.example.chatapp.ui.feature.userlist

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
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

            try {
                userRepository.getAndStoreUsers()
            } catch (e: Exception) {
                Log.d("UserListViewModel", "$e")
            }

            _state.value = _state.value.copy(
                isLoading = false
            )

            userRepository.usersListFlow.collectLatest {
                _state.value = _state.value.copy(
                    usersList = it
                )
            }
        }
    }
}