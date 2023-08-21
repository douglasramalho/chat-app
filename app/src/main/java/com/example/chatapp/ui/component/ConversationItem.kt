package com.example.chatapp.ui.component

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.R
import com.example.chatapp.model.Conversation
import com.example.chatapp.model.ConversationMember
import com.example.chatapp.model.getReceiverMember
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ConversationItem(
    conversation: Conversation,
    onItemClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClicked()
            }
    ) {
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.getReceiverMember().firstName,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                conversation.lastMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = conversation.timestamp,
                    style = MaterialTheme.typography.bodySmall
                )
                if (conversation.unreadCount > 0) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(horizontal = 5.dp),
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewChatCard() {
    ChatAppTheme {
        ConversationItem(
            conversation = Conversation(
                id = "1",
                members = listOf(
                    ConversationMember(
                        id = "1",
                        isSelf = true,
                        username = "tibeca@gmail.com",
                        firstName = "Douglas",
                        lastName = "Motta",
                        profilePictureUrl = null,
                    ),
                    ConversationMember(
                        id = "2",
                        isSelf = false,
                        username = "rahhnascimento@gmail.com",
                        firstName = "Raissa",
                        lastName = "Nascimento",
                        profilePictureUrl = null,
                    )
                ),
                unreadCount = 2,
                lastMessage = null,
                timestamp = "9:00"
            ),
            onItemClicked = {}
        )
    }
}