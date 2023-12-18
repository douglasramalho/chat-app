package com.example.chatapp.ui.feature.conversation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.model.Message
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ChatMessageItem(
    message: Message
) {
    val paddingStart = if (message.isOwnMessage) {
        32.dp
    } else 0.dp

    val paddingEnd = if (!message.isOwnMessage) {
        32.dp
    } else 0.dp

    Box(
        modifier = Modifier
            .wrapContentWidth()
            .padding(start = paddingStart, end = paddingEnd)
            .background(
                color = if (message.isOwnMessage) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                } else MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(32.dp),
            )
    ) {
        Text(
            text = message.text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
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
            )
        )
    }
}