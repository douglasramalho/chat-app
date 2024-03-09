package com.example.chatapp.ui.feature.signin

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatapp.R
import com.example.chatapp.ui.component.ChatPrimaryButton
import com.example.chatapp.ui.component.PrimaryChatTextField
import com.example.chatapp.ui.theme.BackgroundGradient
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun SignInRoute(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
    navigateWhenAuthorized: () -> Unit,
    navigateWhenSignUpClicked: () -> Unit
) {
    val uiState by viewModel.signInUiState.collectAsStateWithLifecycle()

    val currentNavigateWhenAuthorized by rememberUpdatedState(navigateWhenAuthorized)
    LaunchedEffect(uiState) {
        if (uiState.isLoggedIn) {
            currentNavigateWhenAuthorized()
        }
    }

    uiState.errorMessageStringResId?.let { errorMessageStringResId ->
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { viewModel.errorMessageShown() },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.errorMessageShown() }
                ) {
                    Text(text = context.getString(R.string.common_ok))
                }
            },
            title = {
                Text(text = context.getString(R.string.common_generic_error_title))
            },
            text = {
                Text(text = context.getString(errorMessageStringResId))
            }
        )
    }

    SignInScreen(
        modifier = modifier,
        signInUiState = uiState,
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
    signInUiState: SignInUiState,
    onEmailChanged: (username: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    signInClicked: () -> Unit,
    signUpClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = BackgroundGradient
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(78.dp))

        val emailLabel = stringResource(id = R.string.feature_login_email)
        PrimaryChatTextField(
            value = signInUiState.email,
            leftIcon = R.drawable.ic_envelope,
            placeholder = emailLabel,
            errorMessage = signInUiState.emailError?.let { stringResource(it, emailLabel) },
            keyboardType = KeyboardType.Email,
            onInputChange = onEmailChanged::invoke
        )

        Spacer(modifier = Modifier.height(16.dp))

        val passwordLabel = stringResource(id = R.string.feature_login_password)
        PrimaryChatTextField(
            value = signInUiState.password,
            leftIcon = R.drawable.ic_lock,
            placeholder = passwordLabel,
            errorMessage = signInUiState.passwordError?.let { stringResource(it, passwordLabel) },
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password,
            onInputChange = onPasswordChanged::invoke
        )

        Spacer(modifier = Modifier.height(64.dp))

        ChatPrimaryButton(
            title = stringResource(id = R.string.feature_login_button),
            modifier = Modifier.fillMaxWidth(),
            isLoading = signInUiState.isLoading
        ) {
            signInClicked()
        }

        Spacer(modifier = Modifier.height(56.dp))

        val noAccountLabel = stringResource(id = R.string.feature_login_no_account)
        val registerLabel = stringResource(id = R.string.feature_login_register)
        val annotatedString = buildAnnotatedString {
            val text = "$noAccountLabel $registerLabel"
            val startIndex = text.indexOf(registerLabel)
            val endIndex = startIndex + registerLabel.lastIndex + 1

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
                annotation = "register",
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
                signInUiState = SignInUiState(),
                {},
                {},
                {},
                {},
            )
        }
    }
}