package com.example.chatapp.ui.feature.signin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.R
import com.example.chatapp.model.AuthResult
import com.example.chatapp.ui.component.ChatPrimaryButton
import com.example.chatapp.ui.component.PrimaryChatTextField
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun SignInRoute(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
    navigateWhenAuthorized: () -> Unit,
    navigateWhenSignUpClicked: () -> Unit
) {
    val state = viewModel.formState
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
        formState = state,
        onEmailChanged = {
            viewModel.onEvent(SignInUiEvent.UsernameChanged(it))
        },
        onPasswordChanged = {
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
    formState: SignInState,
    onEmailChanged: (username: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    signInClicked: () -> Unit,
    signUpClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        Color(0xFF1E1E1E),
                        Color.Black,
                    ),
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(78.dp))

        PrimaryChatTextField(
            value = formState.email,
            leftIcon = R.drawable.ic_envelope,
            placeholder = "E-mail",
            errorMessage = formState.emailError?.let { context.getString(it, "E-mail") },
            keyboardType = KeyboardType.Email,
            onInputChange = onEmailChanged::invoke
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryChatTextField(
            value = formState.password,
            leftIcon = R.drawable.ic_lock,
            placeholder = "Password",
            errorMessage = formState.passwordError?.let { context.getString(it, "Password") },
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password,
            onInputChange = onPasswordChanged::invoke
        )

        Spacer(modifier = Modifier.height(64.dp))

        ChatPrimaryButton(
            title = "Sign In",
            modifier = Modifier.fillMaxWidth(),
            isLoading = formState.isLoading
        ) {
            signInClicked()
        }

        Spacer(modifier = Modifier.height(56.dp))

        val annotatedString = buildAnnotatedString {
            val text = "Don't have an account? Sign up here"
            val startIndex = text.indexOf("Sign")
            val endIndex = startIndex + 12

            append(text)
            addStyle(
                style = SpanStyle(
                    color = Color.White,
                ),
                start = 0,
                end = startIndex
            )
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

@Preview(device = Devices.NEXUS_5)
@Composable
fun PreviewSignInScreen() {
    ChatAppTheme {
        Surface {
            SignInScreen(
                modifier = Modifier,
                formState = SignInState(),
                {},
                {},
                {},
                {}
            )
        }
    }
}