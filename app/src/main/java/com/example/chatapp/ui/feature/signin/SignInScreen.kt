package com.example.chatapp.ui.feature.signin

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.model.AuthResult
import com.example.chatapp.ui.component.ChatPrimaryButton
import com.example.chatapp.ui.component.ChatTextField
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun SignInRoute(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
    navigateWhenAuthorized: () -> Unit,
    navigateWhenSignUpClicked: () -> Unit
) {
    val state = viewModel.state
    val context = LocalContext.current
    LaunchedEffect(viewModel, context) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    navigateWhenAuthorized()
                }

                is AuthResult.Unauthorized -> {
                    Toast.makeText(
                        context,
                        "You're not authorized",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AuthResult.UnknownError -> {
                    Toast.makeText(
                        context,
                        "An unknown error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    SignInScreen(
        modifier = modifier,
        state = state,
        usernameChanged = {
            viewModel.onEvent(SignInUiEvent.UsernameChanged(it))
        },
        passwordChanged = {
            viewModel.onEvent(SignInUiEvent.PasswordChanged(it))
        },
        signInClicked = {
            viewModel.onEvent(SignInUiEvent.SignIn)
        },
        signUpClicked = {
            navigateWhenSignUpClicked()
        }
    )
}

@Composable
private fun SignInScreen(
    modifier: Modifier,
    state: SignInState,
    usernameChanged: (username: String) -> Unit,
    passwordChanged: (password: String) -> Unit,
    signInClicked: () -> Unit,
    signUpClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        ChatTextField(
            label = "Username:",
            value = state.username
        ) {
            usernameChanged(it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ChatTextField(
            label = "Password:",
            value = state.password,
        ) {
            passwordChanged(it)
        }

        Spacer(modifier = Modifier.height(32.dp))

        ChatPrimaryButton(
            title = "Sign In",
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isLoading
        ) {
            signInClicked()
        }

        Spacer(modifier = Modifier.height(8.dp))

        val annotatedString = buildAnnotatedString {
            val text = "Don't have an account? Sign up here"
            val startIndex = text.indexOf("Sign")
            val endIndex = startIndex + 12

            append(text)
            addStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ),
                start = startIndex,
                end = endIndex
            )

            addStringAnnotation(
                tag = "route",
                annotation = "singup",
                start = startIndex,
                end = endIndex
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ClickableText(
                text = annotatedString
            ) {
                annotatedString
                    .getStringAnnotations("route", it, it)
                    .firstOrNull()?.let {
                        signUpClicked()
                    }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignInScreen() {
    ChatAppTheme {
        Surface {
            SignInScreen(
                modifier = Modifier,
                state = SignInState(),
                {},
                {},
                {},
                {}
            )
        }
    }
}