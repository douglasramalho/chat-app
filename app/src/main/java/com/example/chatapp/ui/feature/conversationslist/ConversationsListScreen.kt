package com.example.chatapp.ui.feature.conversationslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.example.chatapp.model.getReceiverMember
import com.example.chatapp.ui.ChatSocketViewModel
import com.example.chatapp.ui.component.ConversationItem
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.Grey1

@Composable
fun ConversationsListRoute(
    viewModel: ChatSocketViewModel = hiltViewModel(),
    navigateWhenLogout: () -> Unit,
    navigateWhenConversationItemClicked: (receiverId: String) -> Unit,
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

    LaunchedEffect(viewModel) {
        viewModel.logoutResult.collect {
            navigateWhenLogout()
        }
    }

    val state by viewModel.conversationsListState

    ConversationsListScreen(
        conversationsList = state.conversationsList,
        onIconButtonClicked = {
            viewModel.logout()
        },
        onConversationItemClicked = { receiverId ->
            navigateWhenConversationItemClicked(receiverId)
        }
    )
}

@Composable
private fun ConversationsListScreen(
    conversationsList: List<Conversation>,
    onIconButtonClicked: () -> Unit,
    onConversationItemClicked: (receiverId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.inverseSurface
            ),
    ) {
        Text(
            text = "Bem vindo de volta, Douglas",
            modifier = Modifier
                .padding(vertical = 32.dp, horizontal = 20.dp)
                .clickable {
                    onIconButtonClicked()
                },
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.titleLarge
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.extraLarge.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                itemsIndexed(conversationsList) { index, conversation ->
                    ConversationItem(
                        conversation = conversation
                    ) {
                        onConversationItemClicked(conversation.getReceiverMember().id)
                    }

                    if (index < conversationsList.lastIndex) {
                        Divider(
                            color = Grey1,
                            thickness = 1.dp
                        )
                    }
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
                {}
            ) {

            }
        }
    }
}