package com.example.chatapp.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChatPrimaryButton(
    title: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            if (!isLoading) {
                onClick.invoke()
            }
        },
        modifier = modifier
            .height(50.dp),
        enabled = !isLoading
    ) {
        Text(text = title)

        if (isLoading) {
            Spacer(modifier = Modifier.width(4.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
@Preview
fun PreviewChatPrimaryButton() {
    ChatPrimaryButton(title = "Primary button") {

    }
}