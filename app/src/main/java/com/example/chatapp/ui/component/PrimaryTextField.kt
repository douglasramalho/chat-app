package com.example.chatapp.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.R
import com.example.chatapp.ui.extension.isPassword

@Composable
fun PrimaryChatTextField(
    value: String,
    @DrawableRes leftIcon: Int? = null,
    placeholder: String = "",
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    onInputChange: (text: String) -> Unit
) {
    var input by remember {
        mutableStateOf(TextFieldValue(value))
    }

    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    Column {
        OutlinedTextField(
            value = input,
            placeholder = { Text(text = placeholder) },
            leadingIcon = leftIcon?.let {
                @Composable {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            },
            trailingIcon = if (keyboardType.isPassword() && input.text.isNotEmpty()) {
                val visibilityIcon = if (passwordVisibility) {
                    R.drawable.ic_visibility
                } else R.drawable.ic_visibility_off

                @Composable {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            painter = painterResource(id = visibilityIcon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else null,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            onValueChange = {
                input = it
                onInputChange(it.text)
            },
            visualTransformation = if (keyboardType.isPassword()) {
                if (passwordVisibility) {
                    VisualTransformation.None
                } else PasswordVisualTransformation()

            } else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                capitalization = if (keyboardType == KeyboardType.Text) {
                    KeyboardCapitalization.Sentences
                } else KeyboardCapitalization.None,
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (errorMessage != null) {
                    MaterialTheme.colorScheme.error
                } else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
@Preview(showBackground = false)
fun PrimaryChatTextFieldEmailPreview() {
    PrimaryChatTextField(
        value = "",
        leftIcon = R.drawable.ic_envelope,
        placeholder = "E-mail",
        keyboardType = KeyboardType.Email,
        onInputChange = {}
    )
}

@Composable
@Preview(showBackground = false)
fun PrimaryChatTextFieldPasswordPreview() {
    PrimaryChatTextField(
        value = "123456",
        leftIcon = R.drawable.ic_lock,
        placeholder = "Password",
        keyboardType = KeyboardType.Password,
        onInputChange = {}
    )
}

@Composable
@Preview(showBackground = false)
fun PrimaryChatTextFieldErrorPreview() {
    PrimaryChatTextField(
        value = "",
        leftIcon = R.drawable.ic_envelope,
        placeholder = "E-mail",
        errorMessage = "Invalid",
        keyboardType = KeyboardType.Email,
        onInputChange = {}
    )
}