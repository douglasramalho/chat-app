package com.example.chatapp.ui.feature.userslist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.chatapp.R
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.example.chatapp.model.User
import com.example.chatapp.model.UserPreviewParameterProvider
import com.example.chatapp.model.getReceiverMember
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
        Card {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = user.firstName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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