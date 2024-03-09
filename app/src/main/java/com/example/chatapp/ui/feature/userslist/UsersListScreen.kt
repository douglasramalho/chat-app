package com.example.chatapp.ui.feature.userslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.model.User
import com.example.chatapp.model.UserPreviewParameterProvider
import com.example.chatapp.ui.component.ChatScaffold
import com.example.chatapp.ui.component.ChatTopBar
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.Grey1

@Composable
fun UserListRoute(
    viewModel: UsersListViewModel = hiltViewModel(),
    onNavigationClick: () -> Unit,
    navigateWhenUserItemClicked: (userId: String) -> Unit,
) {

    val state by viewModel.state

    UserListScreen(
        users = state.usersList,
        onNavigationClick = onNavigationClick,
    ) { userId ->
        navigateWhenUserItemClicked(userId)
    }
}

@Composable
private fun UserListScreen(
    users: List<User>,
    onNavigationClick: () -> Unit,
    onUserItemClicked: (userId: String) -> Unit,
) {
    ChatScaffold(
        topBar = {
            ChatTopBar(
                title = "Users"
            ) {
                onNavigationClick()
            }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
        ) {
            itemsIndexed(users) { index, user ->
                UserItem(
                    user = user,
                    onItemClicked = {
                        onUserItemClicked(user.id)
                    }
                )

                if (index < users.lastIndex) {
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

@Preview
@Composable
fun PreviewUserListScreen(
    @PreviewParameter(UserPreviewParameterProvider::class)
    users: List<User>
) {
    ChatAppTheme {
        Surface {
            UserListScreen(
                users = users,
                onNavigationClick = {},
                onUserItemClicked = {}
            )
        }
    }
}