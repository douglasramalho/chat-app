package com.example.chatapp.ui.feature.signup

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
import com.example.chatapp.ui.component.ChatSecondaryButton
import com.example.chatapp.ui.component.ChatTextField
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateWhenAuthorized: () -> Unit,
    navigateWhenSigInClicked: () -> Unit,
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

    SignUpScreen(
        modifier = modifier,
        state = state,
        usernameChanged = {
            viewModel.onEvent(SignUpUiEvent.UsernameChanged(it))
        },
        passwordChanged = {
            viewModel.onEvent(SignUpUiEvent.PasswordChanged(it))
        },
        firstNameChanged = {
            viewModel.onEvent(SignUpUiEvent.FirstNameChanged(it))
        },
        lastNameChanged = {
            viewModel.onEvent(SignUpUiEvent.LastNameChanged(it))
        },
        profilePictureChanged = {
            viewModel.onEvent(SignUpUiEvent.ProfilePictureUrlChanged(it))
        },
        signUpClicked = {
            viewModel.onEvent(SignUpUiEvent.SignUp)
        },
        signInClicked = {
            navigateWhenSigInClicked()
        }
    )
}

@Composable
private fun SignUpScreen(
    modifier: Modifier,
    state: SignUpState,
    usernameChanged: (username: String) -> Unit,
    passwordChanged: (password: String) -> Unit,
    firstNameChanged: (firstName: String) -> Unit,
    lastNameChanged: (lastName: String) -> Unit,
    profilePictureChanged: (profilePictureUrl: String?) -> Unit,
    signUpClicked: () -> Unit,
    signInClicked: () -> Unit,
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
            value = state.password
        ) {
            passwordChanged(it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ChatTextField(
            label = "First Name:",
            value = state.firstName
        ) {
            firstNameChanged(it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ChatTextField(
            label = "Last Name:",
            value = state.lastName
        ) {
            lastNameChanged(it)
        }

        Spacer(modifier = Modifier.height(32.dp))

        ChatPrimaryButton(
            title = "Sign Up",
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isLoading
        ) {
            signUpClicked()
        }

        Spacer(modifier = Modifier.height(16.dp))

        val annotatedString = buildAnnotatedString {
            val text = "Do you have an account? Login here"
            val startIndex = text.indexOf("Login")
            val endIndex = startIndex + 10

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
                annotation = "signin",
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
                        signInClicked()
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
            SignUpScreen(
                modifier = Modifier,
                state = SignUpState(),
                {},
                {},
                {},
                {},
                {},
                {},
                {},
            )
        }
    }
}