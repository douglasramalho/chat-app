package com.example.chatapp.ui.feature.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.ui.component.ChatScaffold
import com.example.chatapp.ui.component.ChatTopBar
import com.example.chatapp.ui.component.MessageTextField
import com.example.chatapp.ui.feature.conversation.component.ChatMessageItem
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ConversationRoute(
    conversationViewModel: ConversationViewModel = hiltViewModel(),
    receiverId: String?,
    onNavigationClick: () -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                receiverId?.let {
                    conversationViewModel.connectToSocket()
                    conversationViewModel.onConversation(it)
                }
            }

            if (event == Lifecycle.Event.ON_PAUSE) {
                receiverId?.let {
                    conversationViewModel.closeSocketConnection()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state by conversationViewModel.conversationState.collectAsStateWithLifecycle()
    val messages by conversationViewModel.messagesStateFlow.collectAsStateWithLifecycle()
    val messageText by conversationViewModel.messageTextState

    messages.forEach {
        conversationViewModel.readMessage(it)
    }

    ConversationScreen(
        state,
        messages,
        messageText,
        onNavigationClick,
        conversationViewModel::onMessageChange,
        onSendMessage = { conversationViewModel.sendMessage(receiverId!!) }
    )
}

@Composable
fun ConversationScreen(
    conversationState: ConversationState,
    messages: List<Message>,
    messageText: String,
    onNavigationClick: () -> Unit,
    onMessageChanged: (message: String) -> Unit,
    onSendMessage: () -> Unit,
) {
    val receiver = conversationState.receiver

    ChatScaffold(
        topBar = {
            ChatTopBar(
                customContent = {
                    val profilePicture = receiver?.profilePictureUrl
                        ?: R.drawable.no_profile_image

                    AsyncImage(
                        model = profilePicture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = receiver?.firstName ?: "",
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (conversationState.isOnline) {
                            Text(
                                text = "Online",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            ) {
                onNavigationClick()
            }
        }
    ) {
        ConversationSection(
            messages = messages,
            messageText = messageText,
            onMessageChanged = onMessageChanged,
            onSendMessage = onSendMessage,
        )
    }
}

@Composable
fun ConversationSection(
    messages: List<Message>,
    messageText: String,
    onMessageChanged: (message: String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Column {
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom)
        ) {
            itemsIndexed(messages) { index, item ->
                val previousMessage = if (index > 0) {
                    messages[index - 1]
                } else null

                ChatMessageItem(
                    message = item,
                    previousMessage = previousMessage,
                )

                if (index < messages.lastIndex && item.isOwnMessage && !messages[index + 1].isOwnMessage) {
                    Spacer(modifier = Modifier.height(50.dp))

                }

                if (index < messages.lastIndex && !item.isOwnMessage && messages[index + 1].isOwnMessage) {
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        MessageTextField(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            placeholder = "Type a message",
            value = messageText,
            onInputChange = onMessageChanged,
            onSendClicked = onSendMessage
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun PreviewConversationScreen() {
    ChatAppTheme {
        ConversationScreen(
            ConversationState(
                receiver = User(
                    id = "1",
                    username = "tibeca@gmail.com",
                    "Douglas",
                    "Motta",
                    null
                ),
                messages = emptyList(),
                isOnline = true
            ),
            messages = listOf(
                Message(
                    id = 1,
                    senderId = 1,
                    receiverId = 2,
                    text = "I'm doing well",
                    formattedTime = "15:00",
                    isUnread = false,
                    isOwnMessage = true
                ),
                Message(
                    id = 1,
                    senderId = 1,
                    receiverId = 2,
                    text = "Hello Raissa",
                    formattedTime = "15:00",
                    isUnread = false,
                    isOwnMessage = true
                ),
                Message(
                    id = 1,
                    senderId = 1,
                    receiverId = 2,
                    text = "Hello Douglas",
                    formattedTime = "11:00",
                    isUnread = false,
                    isOwnMessage = false
                ),
                Message(
                    id = 1,
                    senderId = 1,
                    receiverId = 2,
                    text = "How are you doing?",
                    formattedTime = "11:00",
                    isUnread = false,
                    isOwnMessage = true
                ),
            ),
            messageText = "",
            onNavigationClick = {},
            onMessageChanged = {},
            onSendMessage = {},
        )
    }
}