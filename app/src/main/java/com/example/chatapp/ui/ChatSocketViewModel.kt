package com.example.chatapp.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.data.repository.ChatSocketRepository
import com.example.chatapp.data.repository.MessageRepository
import com.example.chatapp.data.repository.SocketResult
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.model.Message
import com.example.chatapp.model.getReceiverMember
import com.example.chatapp.ui.feature.conversation.ConversationState
import com.example.chatapp.ui.feature.conversationslist.ConversationsListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatSocketViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val chatSocketRepository: ChatSocketRepository,
) : ViewModel() {

    private val _messageTextState = mutableStateOf("")
    val messageTextState: State<String> = _messageTextState

    private val _conversationState = mutableStateOf(ConversationState())
    val conversationState: State<ConversationState> = _conversationState

    private val _conversationsListState = mutableStateOf(ConversationsListState())
    val conversationsListState: State<ConversationsListState> = _conversationsListState

    private val logoutChannel = Channel<Unit>()
    val logoutResult = logoutChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            _conversationsListState.value = _conversationsListState.value.copy(
                isLoading = true
            )

            savedStateHandle.get<String>("receiverId")?.let { receiverId ->
                _conversationState.value =
                    _conversationState.value.copy(isLoading = true)

                userRepository.getUserFlowBy(receiverId).collectLatest {
                    _conversationState.value =
                        _conversationState.value.copy(receiver = it)
                }

                chatSocketRepository.getConversationBy2(receiverId).collect {
                    _conversationState.value = _conversationState.value.copy(
                        conversation = it
                    )
                }

                messageRepository.getMessages2(receiverId).collect {
                    _conversationState.value = _conversationState.value.copy(
                        messages = it,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun connectChatSocket() {
        viewModelScope.launch {
            chatSocketRepository.openSession()
                .onEach {
                    when (it) {
                        is SocketResult.Open -> {
                            getConversations()
                        }

                        is SocketResult.NewMessage -> {
                            val message = it.message
                            val newList = conversationState.value.messages.toMutableList().apply {
                                add(0, message)
                            }

                            _conversationState.value =
                                _conversationState.value.copy(messages = newList)
                        }

                        is SocketResult.Conversations -> {
                            _conversationsListState.value = _conversationsListState.value.copy(
                                isLoading = false,
                                conversationsList = it.conversations
                            )
                        }

                        else -> {
                        }
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun onMessageChange(message: String) {
        _messageTextState.value = message
    }

    private fun getConversations() {
        viewModelScope.launch {
            chatSocketRepository.getConversationsList()
        }
    }

    fun readMessage(message: Message) {
        viewModelScope.launch {
            if (!message.isOwnMessage && message.isUnread) {
                chatSocketRepository.sendReadMessage(message.id)
            }
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            chatSocketRepository.sendMessage(
                receiverId = _conversationState.value.receiver?.id ?: "",
                message = messageTextState.value
            )
            _messageTextState.value = ""
        }
    }

    fun closeConnection() {
        viewModelScope.launch {
            chatSocketRepository.closeSession()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            logoutChannel.send(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        closeConnection()
    }
}