package com.example.chatapp.ui.feature.conversation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.model.Message
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ChatMessageItem(
    message: Message,
    previousMessage: Message?,
) {
    Column(
        horizontalAlignment = if (message.isOwnMessage) {
            Alignment.End
        } else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val surfaceColor = if  (message.isOwnMessage) {
            MaterialTheme.colorScheme.tertiary
        } else MaterialTheme.colorScheme.secondary

        val textColor = if (message.isOwnMessage) {
            MaterialTheme.colorScheme.onTertiary
        } else MaterialTheme.colorScheme.onSecondary

        Surface(
            modifier = Modifier
                .wrapContentWidth(),
            shape =  RoundedCornerShape(100.dp),
            color = surfaceColor,
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                color = textColor
            )
        }

        val showTime = if (message.isOwnMessage) {
            message.formattedTime != previousMessage?.formattedTime || !previousMessage.isOwnMessage
        } else message.formattedTime != previousMessage?.formattedTime || previousMessage.isOwnMessage

        if (showTime) {
            Text(
                text = message.formattedTime,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewOwnMessage() {
    ChatAppTheme {
        ChatMessageItem(
            message = Message(
                id = 1,
                senderId = 1,
                receiverId = 2,
                text = "Olá",
                formattedTime = "20:00",
                isUnread = true,
                isOwnMessage = true,
            ),
            previousMessage = Message(
                id = 1,
                senderId = 1,
                receiverId = 2,
                text = "Olá",
                formattedTime = "21:00",
                isUnread = true,
                isOwnMessage = true,
            )
        )
    }
}

@Preview
@Composable
fun PreviewOtherMessage() {
    ChatAppTheme {
        ChatMessageItem(
            message = Message(
                id = 1,
                senderId = 1,
                receiverId = 2,
                text = "Olá",
                formattedTime = "21:00",
                isUnread = true,
                isOwnMessage = false,
            ),
            previousMessage = Message(
                id = 1,
                senderId = 1,
                receiverId = 2,
                text = "Olá",
                formattedTime = "20:00",
                isUnread = true,
                isOwnMessage = true,
            )
        )
    }
}