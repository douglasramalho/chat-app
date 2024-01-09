package com.example.chatapp.ui.feature.conversationslist

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatapp.MainActivity
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.model.getReceiverMember
import com.example.chatapp.ui.ChatSocketViewModel
import com.example.chatapp.ui.component.ChatScaffold
import com.example.chatapp.ui.component.ChatTopBar
import com.example.chatapp.ui.component.ConversationItem
import com.example.chatapp.ui.feature.conversation.ConversationSection
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.Grey1

@Composable
fun ConversationsListRoute(
    windowSizeClass: WindowSizeClass,
    context: Context = LocalContext.current,
    viewModel: ConversationsListViewModel = hiltViewModel(),
    chatSocketViewModel: ChatSocketViewModel = hiltViewModel(
        viewModelStoreOwner = context as MainActivity
    ),
    navigateWhenConversationItemClicked: (receiverId: String) -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                chatSocketViewModel.connectToSocket()
                viewModel.getConversationsList()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val conversationState by chatSocketViewModel.conversationState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUserStateFlow.collectAsStateWithLifecycle()

    if (conversationState.hasUnreadMessages) {
        viewModel.getConversationsList()
    }

    val messageText by chatSocketViewModel.messageTextState

    ConversationsListScreen(
        windowSizeClass = windowSizeClass,
        currentUser = currentUser,
        conversationsList = state.conversationsList,
        messages = conversationState.messages,
        messageText = messageText,
        onConversationItemClicked = { receiverId ->
            navigateWhenConversationItemClicked(receiverId)
        },
        onConversationItemTwoPaneClicked = {
            chatSocketViewModel.onConversation(it)
        },
        chatSocketViewModel::onMessageChange,
        onSendMessage = {
            chatSocketViewModel.sendMessage(it)
            viewModel.getConversationsList()
        }
    )
}

@Composable
private fun ConversationsListScreen(
    windowSizeClass: WindowSizeClass,
    currentUser: User?,
    conversationsList: List<Conversation>,
    messages: List<Message>,
    messageText: String,
    onConversationItemClicked: (receiverId: String) -> Unit,
    onConversationItemTwoPaneClicked: (receiverId: String) -> Unit,
    onMessageChanged: (message: String) -> Unit,
    onSendMessage: (receiverId: String) -> Unit,
) {
    ChatScaffold(
        topBar = {
            ChatTopBar(
                title = "Welcome back, ${currentUser?.firstName}"
            )
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            val showTwoPane = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact
            val modifier = if (showTwoPane) {
                Modifier.width(250.dp)
            } else Modifier.fillMaxWidth()

            var selectedConversationId: String? by rememberSaveable {
                mutableStateOf(null)
            }

            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                itemsIndexed(conversationsList) { index, conversation ->
                    ConversationItem(
                        conversation = conversation
                    ) {
                        if (showTwoPane) {
                            selectedConversationId = conversation.getReceiverMember().id
                            onConversationItemTwoPaneClicked(selectedConversationId!!)
                        } else onConversationItemClicked(conversation.getReceiverMember().id)
                    }

                    if (index < conversationsList.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = Grey1,
                            thickness = 1.dp
                        )
                    }
                }
            }

            if (showTwoPane) {
                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .fillMaxHeight()
                        .background(Color.LightGray)
                )

                selectedConversationId?.let {
                    messages.forEach {
                        // chatSocketViewModel.readMessage(it)
                    }

                    ConversationSection(
                        messages = messages,
                        messageText = messageText,
                        onMessageChanged = onMessageChanged,
                        onSendMessage = {
                            onSendMessage(it)
                        }
                    )
                } ?: Text(text = "Hello")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(device = "spec:width=800dp,height=850.9dp,dpi=440")
@Composable
fun PreviewConversationsListScreen() {
    ChatAppTheme {
        Surface {
            ConversationsListScreen(
                windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(800.dp, 400.dp)),
                currentUser = null,
                conversationsList = listOf(
                    Conversation(
                        id = "1",
                        members = listOf(
                            ConversationMember(
                                id = "1",
                                isSelf = true,
                                username = "",
                                firstName = "Douglas",
                                lastName = "",
                                profilePictureUrl = null
                            ),
                            ConversationMember(
                                id = "2",
                                isSelf = false,
                                username = "",
                                firstName = "Raissa",
                                lastName = "",
                                profilePictureUrl = null
                            )
                        ),
                        unreadCount = 2,
                        lastMessage = "Olá",
                        timestamp = "9:00"
                    ),
                    Conversation(
                        id = "2",
                        members = listOf(
                            ConversationMember(
                                id = "1",
                                isSelf = true,
                                username = "",
                                firstName = "Douglas",
                                lastName = "",
                                profilePictureUrl = null
                            ),
                            ConversationMember(
                                id = "3",
                                isSelf = false,
                                username = "",
                                firstName = "Diogo",
                                lastName = "",
                                profilePictureUrl = null
                            )
                        ),
                        unreadCount = 2,
                        lastMessage = "Como vai?",
                        timestamp = "9:00"
                    ),
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
                onConversationItemClicked = {},
                onConversationItemTwoPaneClicked = {},
                onMessageChanged = {},
                onSendMessage = {},
            )
        }
    }
}