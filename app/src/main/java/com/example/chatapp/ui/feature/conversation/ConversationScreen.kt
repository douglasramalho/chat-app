package com.example.chatapp.ui.feature.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.ui.ChatSocketViewModel
import com.example.chatapp.ui.component.ChatScaffold
import com.example.chatapp.ui.component.ChatTopBar
import com.example.chatapp.ui.component.MessageTextField
import com.example.chatapp.ui.feature.conversation.component.ChatMessageItem
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ConversationRoute(
    chatSocketViewModel: ChatSocketViewModel,
    receiverId: String?,
    onNavigationClick: () -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                chatSocketViewModel.openSocketConnection()
            }

            if (event == Lifecycle.Event.ON_PAUSE) {
                chatSocketViewModel.closeSocketConnection()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isSocketOpen by chatSocketViewModel.socketOpenState.collectAsStateWithLifecycle()
    LaunchedEffect(isSocketOpen) {
        if (isSocketOpen) {
            chatSocketViewModel.onConversation(receiverId!!)
        }
    }

    val state by chatSocketViewModel.conversationState
    val messageText by chatSocketViewModel.messageTextState

    val receiver = state.receiver
    val messages = state.messages
    messages.forEach {
        chatSocketViewModel.readMessage(it)
    }

    ConversationScreen(
        state,
        messageText,
        onNavigationClick,
        chatSocketViewModel::onMessageChange,
        onSendMessage = { chatSocketViewModel.sendMessage(receiverId!!) }
    )
}

@Composable
fun ConversationScreen(
    conversationState: ConversationState,
    messageText: String,
    onNavigationClick: () -> Unit,
    onMessageChanged: (message: String) -> Unit,
    onSendMessage: () -> Unit,
) {
    val receiver = conversationState.receiver
    val messages = conversationState.messages

    ChatScaffold(
        topBar = {
            ChatTopBar(
                customContent = {
                    AsyncImage(
                        model = receiver?.profilePictureUrl,
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
                    if (index == 0) {
                        if (item.isOwnMessage) {
                            Text(
                                text = item.formattedTime,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        } else {
                            Text(
                                text = item.formattedTime,
                                modifier = Modifier
                                    .fillMaxWidth(),
                            )
                        }
                    }

                    if (index > 0) {
                        if (item.isOwnMessage && !messages[index - 1].isOwnMessage) {
                            Text(
                                text = item.formattedTime,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }

                        if (!item.isOwnMessage && messages[index - 1].isOwnMessage) {
                            Text(
                                text = item.formattedTime,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }

                    Box(
                        contentAlignment = if (item.isOwnMessage) {
                            Alignment.CenterEnd
                        } else Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ChatMessageItem(message = item)
                    }

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
                        formattedTime = "Today",
                        isUnread = false,
                        isOwnMessage = true
                    ),
                    Message(
                        id = 1,
                        senderId = 1,
                        receiverId = 2,
                        text = "Hello Douglas",
                        formattedTime = "Today",
                        isUnread = false,
                        isOwnMessage = false
                    ),
                    Message(
                        id = 1,
                        senderId = 1,
                        receiverId = 2,
                        text = "How are you doing?",
                        formattedTime = "Today",
                        isUnread = false,
                        isOwnMessage = true
                    ),
                ),
                isOnline = true
            ),
            messageText = "",
            onNavigationClick = {},
            onMessageChanged = {},
            onSendMessage = {},
        )
    }
}