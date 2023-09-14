package com.example.chatapp.ui.feature.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.model.getReceiverMember
import com.example.chatapp.ui.ChatSocketViewModel

@Composable
fun ConversationRoute(
    viewModel: ChatSocketViewModel = hiltViewModel(),
    onNavigationClick: () -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.connectChatSocket()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.closeConnection()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state by viewModel.conversationState
    val messageText by viewModel.messageTextState

    val receiver = state.receiver
    val messages = state.messages
    messages.forEach {
        viewModel.readMessage(it)
    }

    ConversationScreen(
        receiver,
        messages,
        messageText,
        onNavigationClick,
        viewModel::onMessageChange,
    ) { viewModel.sendMessage() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    receiver: User?,
    messages: List<Message>,
    messageText: String,
    onNavigationClick: () -> Unit,
    onMessageChanged: (message: String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = receiver?.firstName ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.primary)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom)
        ) {
            itemsIndexed(messages) { index, item ->
                Box(
                    contentAlignment = if (item.isOwnMessage) {
                        Alignment.CenterEnd
                    } else Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .drawBehind {
                                val cornerRadius = 10.dp.toPx()
                                val triangleWidth = 10.dp.toPx()

                                val trianglePath = Path().apply {
                                    if (item.isOwnMessage) {
                                        if (checkPreviousMessage(messages, index, true)) {
                                            moveTo(size.width - cornerRadius, 0f)
                                            lineTo(size.width, triangleWidth)
                                            lineTo(size.width + triangleWidth, 0f)
                                            close()
                                        }
                                    } else {
                                        if (checkPreviousMessage(messages, index, false)) {
                                            moveTo(0 - cornerRadius, 0f)
                                            lineTo(0f, triangleWidth)
                                            lineTo(triangleWidth, 0f)
                                            close()
                                        }
                                    }
                                }
                                drawPath(
                                    path = trianglePath,
                                    color = if (item.isOwnMessage) {
                                        Color.Green
                                    } else Color.LightGray
                                )
                            }
                            .background(
                                color = if (item.isOwnMessage) {
                                    Color.Green
                                } else Color.LightGray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = item.text
                        )
                        Text(
                            text = item.formattedTime,
                            modifier = Modifier
                                .align(Alignment.End)
                                .paddingFromBaseline(top = 24.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }

                if (index < messages.lastIndex && item.isOwnMessage && !messages[index + 1].isOwnMessage) {
                    Spacer(modifier = Modifier.height(50.dp))
                }

                if (index < messages.lastIndex && !item.isOwnMessage && messages[index + 1].isOwnMessage) {
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageChanged::invoke,
                placeholder = {
                    Text(text = "Enter a message")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            IconButton(onClick = {
                onSendMessage()
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
            }
        }
    }
}

private fun checkPreviousMessage(
    messages: List<Message>,
    index: Int,
    isOwnMessage: Boolean
): Boolean {
    val nextMessageValidation = if (isOwnMessage) {
        index < messages.lastIndex && !messages[index + 1].isOwnMessage
    } else index < messages.lastIndex && messages[index + 1].isOwnMessage

    return messages.size == 1 || nextMessageValidation || index == messages.lastIndex
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun PreviewConversationScreen() {
    ConversationScreen(
        receiver = User(
            id = "1",
            username = "tibeca@gmail.com",
            "Douglas",
            "Motta",
            null
        ),
        messages = listOf(
            Message(
                id = "1",
                senderId = "1",
                receiverId = "2",
                text = "I'm doing well",
                formattedTime = "Today",
                isUnread = false,
                isOwnMessage = true
            ),
            Message(
                id = "1",
                senderId = "1",
                receiverId = "2",
                text = "Hello Raissa",
                formattedTime = "Today",
                isUnread = false,
                isOwnMessage = true
            ),
            Message(
                id = "1",
                senderId = "1",
                receiverId = "2",
                text = "Hello Douglas",
                formattedTime = "Today",
                isUnread = false,
                isOwnMessage = false
            ),
            Message(
                id = "1",
                senderId = "1",
                receiverId = "2",
                text = "How are you doing?",
                formattedTime = "Today",
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