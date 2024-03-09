package com.example.chatapp.ui.feature.signup

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatapp.R
import com.example.chatapp.ui.component.ChatPrimaryButton
import com.example.chatapp.ui.component.ProfilePicture
import com.example.chatapp.ui.component.SecondaryTextField
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateWhenAuthorized: () -> Unit,
    navigateWhenSigInClicked: () -> Unit,
) {
    val signUpUiState by viewModel.signUpUiState.collectAsStateWithLifecycle()

    val currentNavigateWhenAuthorized by rememberUpdatedState(navigateWhenAuthorized)
    LaunchedEffect(signUpUiState) {
        if (signUpUiState.isLoggedIn) {
            currentNavigateWhenAuthorized()
        }
    }

    signUpUiState.errorMessageStringResId?.let {
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = viewModel::errorMessageShown,
            confirmButton = {
                TextButton(
                    onClick = viewModel::errorMessageShown
                ) {
                    Text(
                        text = context.getString(R.string.common_ok),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            title = {
                Text(
                    text = context.getString(R.string.common_generic_error_title),
                )
            },
            text = {
                Text(
                    text = context.getString(it),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
        )
    }

    SignUpScreen(
        modifier = modifier,
        signUpUiState = signUpUiState,
        onPhotoSelected = {
            viewModel.onEvent(SignUpEvent.ProfilePhotoPathChanged(it))
        },
        onFirstNameChanged = {
            viewModel.onEvent(SignUpEvent.FirstNameChanged(it))
        },
        onLastNameChanged = {
            viewModel.onEvent(SignUpEvent.LastNameChanged(it))
        },
        onEmailChanged = {
            viewModel.onEvent(SignUpEvent.EmailChanged(it))
        },
        onPasswordChanged = {
            viewModel.onEvent(SignUpEvent.PasswordChanged(it))
        },
        onPasswordConfirmationChanged = {
            viewModel.onEvent(SignUpEvent.PasswordConfirmationChanged(it))
        },
        signUpClicked = {
            viewModel.onEvent(SignUpEvent.Submit)
        },
        signInClicked = {
            navigateWhenSigInClicked()
        }
    )
}

@Composable
private fun SignUpScreen(
    modifier: Modifier,
    signUpUiState: SignUpUiState,
    onPhotoSelected: (path: String?) -> Unit,
    onFirstNameChanged: (firstName: String) -> Unit,
    onLastNameChanged: (lastName: String) -> Unit,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    onPasswordConfirmationChanged: (password: String) -> Unit,
    signUpClicked: () -> Unit,
    signInClicked: () -> Unit,
) {
    val context = LocalContext.current

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
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.weight(1f)
        )

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.extraLarge.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePicture(
                onPhotoSelected = onPhotoSelected::invoke
            )

            Spacer(modifier = Modifier.height(30.dp))

            val firstNameLabel = stringResource(id = R.string.feature_sign_up_first_name)
            SecondaryTextField(
                label = firstNameLabel,
                value = signUpUiState.firstName,
                errorMessage = signUpUiState.firstNameError?.let {
                    context.getString(it, firstNameLabel)
                },
                onInputChange = onFirstNameChanged::invoke
            )

            Spacer(modifier = Modifier.height(16.dp))

            val lastNameLabel = stringResource(id = R.string.feature_sign_up_last_name)
            SecondaryTextField(
                label = lastNameLabel,
                value = signUpUiState.lastName,
                errorMessage = signUpUiState.lastNameError?.let {
                    context.getString(it, lastNameLabel)
                },
                onInputChange = onLastNameChanged::invoke
            )

            Spacer(modifier = Modifier.height(16.dp))

            val emailLabel = stringResource(id = R.string.feature_sign_up_email)
            SecondaryTextField(
                label = emailLabel,
                value = signUpUiState.email,
                keyboardType = KeyboardType.Email,
                errorMessage = signUpUiState.emailError?.let {
                    context.getString(it, emailLabel)
                },
                onInputChange = onEmailChanged::invoke
            )

            Spacer(modifier = Modifier.height(16.dp))

            var password by remember {
                mutableStateOf("")
            }

            var passwordConfirmation by remember {
                mutableStateOf("")
            }

            val extraText = if (password.isNotEmpty() && password == passwordConfirmation) {
                stringResource(id = R.string.feature_sign_up_passwords_match)
            } else ""

            SecondaryTextField(
                label = stringResource(id = R.string.feature_sign_up_password),
                value = signUpUiState.password,
                extraText = extraText,
                errorMessage = signUpUiState.passwordError?.let {
                    context.getString(it)
                },
                keyboardType = KeyboardType.Password
            ) {
                password = it
                onPasswordChanged(it)
            }

            Spacer(modifier = Modifier.height(32.dp))

            SecondaryTextField(
                label = stringResource(id = R.string.feature_sign_up_password_confirmation),
                value = signUpUiState.passwordConfirmation,
                extraText = extraText,
                errorMessage = signUpUiState.passwordError?.let {
                    context.getString(it)
                },
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ) {
                passwordConfirmation = it
                onPasswordConfirmationChanged(it)
            }

            Spacer(modifier = Modifier.height(36.dp))

            ChatPrimaryButton(
                title = stringResource(id = R.string.feature_sign_up_button),
                modifier = Modifier.fillMaxWidth(),
                isLoading = signUpUiState.isLoading,
                onClick = signUpClicked::invoke
            )

            Spacer(modifier = Modifier.height(16.dp))

            val hasAccountLabel = stringResource(id = R.string.feature_sign_up_has_account)
            val loginHereLabel = stringResource(id = R.string.feature_sign_up_login_here)
            val annotatedString = buildAnnotatedString {
                val text = "$hasAccountLabel $loginHereLabel"
                val startIndex = text.indexOf(loginHereLabel)
                val endIndex = startIndex + loginHereLabel.lastIndex + 1

                append(text)
                addStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface,
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
                    annotation = "login",
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

            Spacer(modifier = Modifier.height(16.dp))
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
                signUpUiState = SignUpUiState(),
                {},
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