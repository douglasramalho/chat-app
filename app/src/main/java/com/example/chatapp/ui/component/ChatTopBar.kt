package com.example.chatapp.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.R
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun ChatTopBar(
    @DrawableRes navigationIcon: Int = R.drawable.ic_arrow_back,
    title: String? = null,
    customContent: @Composable (() -> Unit)? = null,
    onNavigationClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        onNavigationClick?.let {
            Icon(
                painter = painterResource(id = navigationIcon),
                contentDescription = "Go back to the previous screen",
                modifier = Modifier.clickable {
                    onNavigationClick()
                },
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        title?.let {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        } ?: customContent?.invoke()
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF00BCCE)
fun PreviewChatTopBarWithTitle() {
    ChatAppTheme {
        ChatTopBar(
            title = "Title",
            onNavigationClick = {}
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF00BCCE)
fun PreviewChatTopBarWithCustomContent() {
    ChatAppTheme {
        ChatTopBar(
            customContent = {
                Row {
                    Image(
                        painterResource(id = R.drawable.ic_upload_photo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inverseOnSurface)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Raissa",
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Online",
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            },
            onNavigationClick = {}
        )
    }
}