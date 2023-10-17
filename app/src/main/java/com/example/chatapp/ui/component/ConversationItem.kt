package com.example.chatapp.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
    val profilePicture = conversation.getReceiverMember().profilePictureUrl
        ?: R.drawable.ic_upload_photo

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClicked()
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = profilePicture,
                contentDescription = null,
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.getReceiverMember().firstName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )

                conversation.lastMessage?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it.replace("\n", " "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = conversation.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))

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

@Preview(showBackground = true)
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
                lastMessage = "Olá",
                timestamp = "9:00"
            ),
            onItemClicked = {}
        )
    }
}