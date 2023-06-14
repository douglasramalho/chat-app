package com.example.chatapp.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTextField(
    label: String = "",
    value: String,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    onInputChange: (text: String) -> Unit
) {
    var input by remember {
        mutableStateOf(TextFieldValue(value))
    }

    Column {
        if (label.isNotEmpty()) {
            Text(text = label)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = input,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(4.dp),
            onValueChange = {
                input = it
                onInputChange(it.text)
            },
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
        )
    }
}