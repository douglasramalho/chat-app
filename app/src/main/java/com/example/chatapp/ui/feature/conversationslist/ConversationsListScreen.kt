package com.example.chatapp.ui.feature.conversationslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatapp.LocalActivity
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.example.chatapp.model.User
import com.example.chatapp.model.getReceiverMember
import com.example.chatapp.ui.ChatSocketViewModel
import com.example.chatapp.ui.component.ChatScaffold
import com.example.chatapp.ui.component.ChatTopBar
import com.example.chatapp.ui.component.ConversationItem
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.Grey1

@Composable
fun ConversationsListRoute(
    viewModel: ChatSocketViewModel = hiltViewModel(LocalActivity.current),
    navigateWhenConversationItemClicked: (receiverId: String) -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.getConversations()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentUser by viewModel.currentUserStateFlow.collectAsStateWithLifecycle()
    val state by viewModel.conversationsListState

    ConversationsListScreen(
        currentUser = currentUser,
        conversationsList = state.conversationsList,
        onConversationItemClicked = { receiverId ->
            navigateWhenConversationItemClicked(receiverId)
        }
    )
}

@Composable
private fun ConversationsListScreen(
    currentUser: User?,
    conversationsList: List<Conversation>,
    onConversationItemClicked: (receiverId: String) -> Unit
) {
    ChatScaffold(
        topBar = {
            ChatTopBar(
                title = "Welcome back, ${currentUser?.firstName}"
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            itemsIndexed(conversationsList) { index, conversation ->
                ConversationItem(
                    conversation = conversation
                ) {
                    onConversationItemClicked(conversation.getReceiverMember().id)
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
    }
}

@Preview
@Composable
fun PreviewConversationsListScreen() {
    ChatAppTheme {
        Surface {
            ConversationsListScreen(
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
            ) {

            }
        }
    }
}