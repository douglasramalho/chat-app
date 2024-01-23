package com.example.chatapp.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.ui.theme.ChatAppTheme

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
            .height(64.dp)
            .shadow(
                4.dp,
                shape = CircleShape,
                ambientColor = Color.White,
                spotColor = Color.White
            ),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )

        if (isLoading) {
            Spacer(modifier = Modifier.width(4.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                trackColor = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
@Preview
fun ChatPrimaryButtonPreview() {
    ChatAppTheme {
        ChatPrimaryButton(
            title = "Primary button",
            isLoading = true,
        ) {

        }
    }
}