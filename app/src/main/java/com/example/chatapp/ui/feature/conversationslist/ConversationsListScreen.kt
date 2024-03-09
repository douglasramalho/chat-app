package com.example.chatapp.ui.feature.conversationslist

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.example.chatapp.model.User
import com.example.chatapp.model.getReceiverMember
import com.example.chatapp.ui.component.ChatScaffold
import com.example.chatapp.ui.component.ChatTopBar
import com.example.chatapp.ui.component.ConversationItem
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.Grey1
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun ConversationsListRoute(
    viewModel: ConversationsListViewModel = hiltViewModel(),
    navigateWhenConversationItemClicked: (receiverId: String) -> Unit,
) {
    val currentUser by viewModel.currentUserStateFlow.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.getConversationsList()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    ConversationsListScreen(
        currentUser = currentUser,
        conversationsList = state.conversationsList,
        onConversationItemClicked = { receiverId ->
            navigateWhenConversationItemClicked(receiverId)
        },
    )
}

@Composable
private fun ConversationsListScreen(
    currentUser: User?,
    conversationsList: List<Conversation>,
    onConversationItemClicked: (receiverId: String) -> Unit,
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Grey1
                        )
                    }
                }
            }
        }
    }

    NotificationPermissionEffect()
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    // Permission requests should only be made from an Activity Context, which is not present
    // in previews
    if (LocalInspectionMode.current) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val notificationsPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

@Preview()
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
                onConversationItemClicked = {},
            )
        }
    }
}