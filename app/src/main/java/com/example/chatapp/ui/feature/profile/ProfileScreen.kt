package com.example.chatapp.ui.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.ui.component.ChatScaffold
import com.example.chatapp.ui.component.ChatTopBar
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateWhenLogout: () -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.logoutResult.collect {
            navigateWhenLogout()
        }
    }

    ProfileScreen(
        onLogoutClicked = {
            viewModel.logout()
        },
    )
}

@Composable
private fun ProfileScreen(
    onLogoutClicked: () -> Unit,
) {
    ChatScaffold(
        topBar = {
            ChatTopBar(
                title = "Profile"
            )
        }
    ) {
        Text(
            text = "Logout",
            modifier = Modifier
                .padding(32.dp)
                .clickable {
                onLogoutClicked()
            }
        )
    }
}

@Preview
@Composable
fun PreviewProfileScreen() {
    ChatAppTheme {
        Surface {
            ProfileScreen {
            }
        }
    }
}