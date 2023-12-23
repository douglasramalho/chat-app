package com.example.chatapp.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.ChatSocketRepository
import com.example.chatapp.data.repository.MessageRepository
import com.example.chatapp.data.repository.SocketResult
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.model.Message
import com.example.chatapp.ui.feature.conversation.ConversationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatSocketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val messageRepository: MessageRepository,
    private val chatSocketRepository: ChatSocketRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _messageTextState = mutableStateOf("")
    val messageTextState: State<String> = _messageTextState

    private val _conversationState = mutableStateOf(ConversationState())
    val conversationState: State<ConversationState> = _conversationState

    private val _receiverIdFlow = savedStateHandle.getStateFlow<String?>("receiverId", null)

    fun openSocketConnection(
        onConversationsList: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            chatSocketRepository.openSession { isOpen ->
                if (isOpen) {
                    _receiverIdFlow.value?.let {
                        onConversation(it)
                    }
                }
            }.onEach {
                when (it) {
                    is SocketResult.NewMessage -> {
                        val message = it.message
                        val newList = conversationState.value.messages.toMutableList().apply {
                            add(0, message)
                        }

                        _conversationState.value =
                            _conversationState.value.copy(messages = newList)
                    }

                    is SocketResult.UnreadStatus -> {
                        onConversationsList?.invoke()
                    }

                    is SocketResult.ActiveStatus -> {
                        _conversationState.value.receiver?.let { receiver ->
                            _conversationState.value = _conversationState.value.copy(
                                isOnline = it.activeUserIds.contains(
                                    receiver.id.toInt()
                                )
                            )
                        }
                    }

                    SocketResult.Empty -> {
                    }

                    SocketResult.Error -> {

                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun onConversation(receiverId: String) {
        getOnlineStatus()
        getReceiverUser(receiverId)
        getMessages(receiverId)
    }

    private fun getReceiverUser(receiverId: String) {
        viewModelScope.launch {
            userRepository.getUser(receiverId)
                .collect { resultStatus ->
                    when (resultStatus) {
                        is ResultStatus.Loading -> {
                            _conversationState.value = _conversationState.value.copy(
                                isLoading = true
                            )
                        }

                        is ResultStatus.Error -> {

                        }

                        is ResultStatus.Success -> {
                            _conversationState.value = _conversationState.value.copy(
                                isLoading = false,
                                receiver = resultStatus.data
                            )
                        }
                    }
                }
        }
    }

    private fun getMessages(receiverId: String) {
        viewModelScope.launch {
            messageRepository.getMessages(receiverId)
                .collect { resultStatus ->
                    when (resultStatus) {
                        is ResultStatus.Error -> {
                        }

                        ResultStatus.Loading -> {
                            _conversationState.value = _conversationState.value.copy(
                                isLoading = true
                            )
                        }

                        is ResultStatus.Success -> {
                            _conversationState.value = _conversationState.value.copy(
                                messages = resultStatus.data,
                                isLoading = false
                            )
                        }
                    }
                }
        }
    }

    fun onMessageChange(message: String) {
        _messageTextState.value = message
    }

    private fun getOnlineStatus() {
        viewModelScope.launch {
            chatSocketRepository.getOnlineStatus()
        }
    }

    fun readMessage(message: Message) {
        viewModelScope.launch {
            if (!message.isOwnMessage && message.isUnread) {
                chatSocketRepository.sendReadMessage(message.id)
            }
        }
    }

    fun sendMessage(receiverId: String) {
        viewModelScope.launch {
            val text = messageTextState.value.trim()
            if (text.isNotEmpty()) {
                chatSocketRepository.sendMessage(
                    receiverId = receiverId,
                    message = text
                )
                _messageTextState.value = ""
            }
        }
    }

    fun closeSocketConnection() {
        viewModelScope.launch {
            chatSocketRepository.closeSession()
        }
    }

    override fun onCleared() {
        super.onCleared()
        closeSocketConnection()
    }
}