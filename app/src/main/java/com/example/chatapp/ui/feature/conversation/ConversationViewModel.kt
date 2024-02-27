package com.example.chatapp.ui.feature.conversation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.ChatSocketRepository
import com.example.chatapp.data.repository.MessageRepository
import com.example.chatapp.data.repository.SocketResult
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val chatSocketRepository: ChatSocketRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _messageTextState = mutableStateOf("")
    val messageTextState: State<String> = _messageTextState

    private val _conversationState = MutableStateFlow(ConversationState())
    val conversationState = _conversationState.asStateFlow()

    val messagesStateFlow = messageRepository.messagesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun connectToSocket() {
        viewModelScope.launch {
            chatSocketRepository.openSession()
            chatSocketRepository.observeSocketResult()
                .collect { socketResult ->
                    when (socketResult) {
                        is SocketResult.NewMessage -> {
                            val message = socketResult.message
                            messageRepository.saveNewMessage(message)
                            val newList = conversationState.value.messages.toMutableList().apply {
                                add(0, message)
                            }

                            _conversationState.update {
                                it.copy(messages = newList)
                            }
                        }

                        is SocketResult.UnreadStatus -> {
                            _conversationState.update {
                                it.copy(hasUnreadMessages = socketResult.hasConversationsUnread)
                            }
                        }

                        is SocketResult.ActiveStatus -> {
                            _conversationState.value.receiver?.let { receiver ->
                                _conversationState.value = _conversationState.value.copy(
                                    isOnline = socketResult.activeUserIds.contains(
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
                }
        }
    }

    fun onConversation(receiverId: String) {
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
            messageRepository.getAndStoreMessages(receiverId)
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