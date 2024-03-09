package com.example.chatapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.Grey1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: String = "",
    onInputChange: (text: String) -> Unit,
    onSendClicked: () -> Unit,
) {
    var input by remember { mutableStateOf(value) }

    BasicTextField(
        value = value,
        onValueChange = {
            input = it
            onInputChange(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Grey1,
                shape = ShapeDefaults.ExtraLarge.copy(CornerSize(64.dp))
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = ShapeDefaults.ExtraLarge.copy(CornerSize(64.dp))
            ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Default,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        decorationBox = { innerTextField ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp, max = 120.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TextFieldDefaults.DecorationBox(
                            value = input,
                            innerTextField = innerTextField,
                            enabled = false,
                            singleLine = false,
                            placeholder = { Text(text = placeholder) },
                            visualTransformation = VisualTransformation.None,
                            interactionSource = MutableInteractionSource()
                        ) {

                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(33.dp)
                            .width(1.dp)
                            .background(color = Grey1)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Surface(
                        modifier = Modifier
                            .size(33.dp)
                            .clickable {
                                onSendClicked()
                            },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Send,
                            contentDescription = "Send message",
                            modifier = Modifier.padding(4.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    )
}

@Composable
@Preview
fun MessageTextFieldPreview() {
    ChatAppTheme {
        MessageTextField(
            placeholder = "Type a message",
            value = "",
            onInputChange = {},
            onSendClicked = {},
        )
    }
}