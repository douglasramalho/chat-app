package com.example.chatapp.ui.feature.conversationslist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.data.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationsListViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = mutableStateOf(ConversationsListState())
    val state: State<ConversationsListState> = _state

    private val logoutChannel = Channel<Unit>()
    val logoutResult = logoutChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )
            conversationRepository.getConversations().collect {
                _state.value = _state.value.copy(
                    isLoading = false,
                    conversationsList = it
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            logoutChannel.send(Unit)
        }
    }
}