package com.example.chatapp.ui.feature.conversationlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.chatapp.R
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.example.chatapp.ui.ChatSocketViewModel
import com.example.chatapp.ui.component.ConversationCard
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ConversationsListRoute(
    viewModel: ChatSocketViewModel = hiltViewModel(),
    navigateWhenLogout: () -> Unit,
    navigateWhenConversationItemClicked: (conversationId: String) -> Unit,
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

    val state by viewModel.conversationListState

    ConversationsListScreen(
        conversationsList = state.conversationsList,
        onIconButtonClicked = {
            viewModel.logout()
        },
        onConversationItemClicked = { conversationId ->
            navigateWhenConversationItemClicked(conversationId)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationsListScreen(
    conversationsList: List<Conversation>,
    onIconButtonClicked: () -> Unit,
    onConversationItemClicked: (conversationId: String) -> Unit
) {

    Row(modifier = Modifier.fillMaxSize()) {
        Column {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "DroidZap",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = {
                        onIconButtonClicked()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = "",
                            modifier = Modifier
                                .clip(CircleShape),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.primary)
            )

            LazyColumn {
                items(conversationsList) { conversation ->
                    ConversationCard(
                        conversation = conversation
                    ) {
                        onConversationItemClicked(conversation.id)
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
                                id = "",
                                isSelf = true,
                                username = "",
                                firstName = "Douglas",
                                lastName = "",
                                profilePictureUrl = null
                            )
                        ),
                        unreadCount = 2,
                        lastMessage = null,
                        timestamp = "9:00"
                    ),
                    Conversation(
                        id = "2",
                        members = listOf(
                            ConversationMember(
                                id = "",
                                isSelf = false,
                                username = "",
                                firstName = "Raissa",
                                lastName = "",
                                profilePictureUrl = null
                            )
                        ),
                        unreadCount = 2,
                        lastMessage = null,
                        timestamp = "9:00"
                    ),
                ),
                {}
            ) {

            }
        }
    }
}