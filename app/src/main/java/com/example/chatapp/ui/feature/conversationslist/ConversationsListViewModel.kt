package com.example.chatapp.ui.feature.conversationslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.ConversationRepository
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.data.util.ResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationsListViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationsListState())
    val state = _state.asStateFlow()

    val currentUserStateFlow = userRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun getConversationsList() {
        viewModelScope.launch {
            conversationRepository.getConversations().collect { resultStatus ->
                when (resultStatus) {
                    is ResultStatus.Error -> {

                    }

                    ResultStatus.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultStatus.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            conversationsList = resultStatus.data
                        )
                    }
                }
            }
        }
    }
}