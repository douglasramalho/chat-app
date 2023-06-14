package com.example.chatapp.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChatSecondaryButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = { onClick.invoke() },
        modifier = modifier
            .height(50.dp)
    ) {
        Text(text = title)
    }
}

@Preview
@Composable
fun PreviewSecondaryButton() {
    Surface {
        ChatSecondaryButton(title = "Secondary button") {

        }
    }
}