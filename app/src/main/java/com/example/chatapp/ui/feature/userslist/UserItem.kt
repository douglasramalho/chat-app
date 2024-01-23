package com.example.chatapp.ui.feature.userslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.model.User
import com.example.chatapp.model.UserPreviewParameterProvider
import com.example.chatapp.model.profilePicture
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun UserItem(
    user: User,
    onItemClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClicked()
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = user.profilePicture(),
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = user.firstName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
fun PreviewChatCard(
    @PreviewParameter(UserPreviewParameterProvider::class)
    users: List<User>
) {
    ChatAppTheme {
        UserItem(
            user = users[0],
            onItemClicked = {}
        )
    }
}