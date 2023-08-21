package com.example.chatapp.ui.feature.userslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.model.User
import com.example.chatapp.model.UserPreviewParameterProvider
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun UsersListRoute(
    viewModel: UsersListViewModel = hiltViewModel(),
    navigateWhenUserItemClicked: (userId: String) -> Unit,
) {

    val state by viewModel.state

    UsersListScreen(
        users = state.usersList
    ) { userId ->
        navigateWhenUserItemClicked(userId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsersListScreen(
    users: List<User>,
    onUserItemClicked: (userId: String) -> Unit,
) {

    Row(modifier = Modifier.fillMaxSize()) {
        Column {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Users",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.primary)
            )

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
            ) {
                items(users) { user ->
                    UserItem(
                        user = user,
                        onItemClicked = {
                            onUserItemClicked(user.id)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewUsersListScreen(
    @PreviewParameter(UserPreviewParameterProvider::class)
    users: List<User>
) {
    ChatAppTheme {
        Surface {
            UsersListScreen(
                users = users,
                onUserItemClicked = {}
            )
        }
    }
}