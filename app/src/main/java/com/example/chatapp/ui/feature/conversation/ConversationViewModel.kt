package com.example.chatapp.ui.feature.conversation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.ChatSocketRepository
import com.example.chatapp.data.repository.ConversationRepository
import com.example.chatapp.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val messageRepository: MessageRepository,
    private val chatSocketRepository: ChatSocketRepository,
) : ViewModel() {

    private val _conversationState = mutableStateOf(ConversationState())
    val conversationState: State<ConversationState> = _conversationState

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("conversationId")?.let { conversationId ->
                _conversationState.value = _conversationState.value.copy(isLoading = true)
                messageRepository.getMessages(conversationId).combine(
                    chatSocketRepository.getConversationBy(conversationId)
                ) { messages, conversation ->
                    conversation to messages
                }.collect {
                    _conversationState.value = _conversationState.value.copy(
                        conversation = it.first,
                        messages = it.second,
                        isLoading = false
                    )
                }
            }
        }
    }
}