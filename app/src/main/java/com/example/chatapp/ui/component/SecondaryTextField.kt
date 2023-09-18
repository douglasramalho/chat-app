package com.example.chatapp.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatapp.R
import com.example.chatapp.ui.extension.isPassword
import com.example.chatapp.ui.theme.ColorSuccess

@Composable
fun SecondaryTextField(
    label: String,
    value: String = "",
    extraText: String = "",
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onInputChange: (text: String) -> Unit
) {
    var input by remember { mutableStateOf(value) }

    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    BasicTextField(
        value = input,
        onValueChange = {
            input = it
            onInputChange(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White),
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            capitalization = if (keyboardType == KeyboardType.Text) {
                KeyboardCapitalization.Sentences
            } else KeyboardCapitalization.None,
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        singleLine = true,
        maxLines = 1,
        visualTransformation = if (keyboardType.isPassword()) {
            if (passwordVisibility) {
                VisualTransformation.None
            } else PasswordVisualTransformation()

        } else VisualTransformation.None,
        decorationBox = { innerTextField ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bottomBorder(
                            strokeWidth = 1.dp,
                            color = errorMessage?.let {
                                MaterialTheme.colorScheme.error
                            } ?: Color.Black
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            //this must be called once and in any part of this lambda we want to put it
                            Box(modifier = Modifier.weight(1f)) {
                                innerTextField()
                            }

                            Text(
                                text = extraText,
                                modifier = Modifier.padding(4.dp),
                                color = ColorSuccess,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (keyboardType.isPassword() && input.isNotEmpty()) {
                        val visibilityIcon = if (passwordVisibility) {
                            R.drawable.ic_visibility
                        } else R.drawable.ic_visibility_off

                        IconButton(
                            onClick = {
                                passwordVisibility = !passwordVisibility
                            },
                            modifier = Modifier
                                .size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = visibilityIcon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@Composable
@Preview
fun SecondaryTextFieldNormalPreview() {
    SecondaryTextField(
        label = "First name",
        value = "Douglas",
    ) {

    }
}

@Composable
@Preview
fun SecondaryTextFieldPasswordPreview() {
    SecondaryTextField(
        label = "Password",
        value = "123456",
        extraText = "as senhas são iguais",
        keyboardType = KeyboardType.Password
    ) {

    }
}

@Composable
@Preview
fun SecondaryTextFieldNormalWithErroPreview() {
    SecondaryTextField(
        label = "First name",
        value = "Douglas",
        errorMessage = "Error"
    ) {

    }
}