package com.example.chatapp.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.ChatSocketRepository
import com.example.chatapp.data.repository.MessageRepository
import com.example.chatapp.data.repository.SocketResult
import com.example.chatapp.data.repository.UserRepository
import com.example.chatapp.model.Message
import com.example.chatapp.ui.feature.conversation.ConversationState
import com.example.chatapp.ui.feature.conversationslist.ConversationsListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatSocketViewModel @Inject constructor(
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

    val currentUserStateFlow = userRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )


    fun init(receiverId: String) {
        getOnlineStatus()

        viewModelScope.launch {
            _conversationsListState.value = _conversationsListState.value.copy(
                isLoading = true
            )

            _conversationState.value =
                _conversationState.value.copy(isLoading = true)

            userRepository.getUserFlowBy(receiverId).collectLatest {
                _conversationState.value =
                    _conversationState.value.copy(receiver = it)
            }

            messageRepository.getMessages(receiverId)
                .catch {

                }
                .collect {
                    _conversationState.value = _conversationState.value.copy(
                        messages = it,
                        isLoading = false
                    )
                }
        }
    }

    fun connectChatSocket() {
        viewModelScope.launch {
            chatSocketRepository.openSession()
                .onEach {
                    when (it) {
                        is SocketResult.Open -> {
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

                        is SocketResult.OnlineStatus -> {
                            _conversationState.value.receiver?.let { receiver ->
                                _conversationState.value = _conversationState.value.copy(
                                    isOnline = it.onlineUserIds.contains(
                                        receiver.id.toInt()
                                    )
                                )
                            }
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

    fun getConversations() {
        viewModelScope.launch {
            chatSocketRepository.getConversationsList()
        }
    }

    private fun getOnlineStatus() {
        viewModelScope.launch {
            delay(1_000)
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

    fun sendMessage() {
        viewModelScope.launch {
            val text = messageTextState.value.trim()
            if (text.isNotEmpty()) {
                chatSocketRepository.sendMessage(
                    receiverId = _conversationState.value.receiver?.id ?: "",
                    message = text
                )
                _messageTextState.value = ""
            }
        }
    }

    fun closeConnection() {
        viewModelScope.launch {
            chatSocketRepository.closeSession()
        }
    }

    override fun onCleared() {
        super.onCleared()
        closeConnection()
    }
}