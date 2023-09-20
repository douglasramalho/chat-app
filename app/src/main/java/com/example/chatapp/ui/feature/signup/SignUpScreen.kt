package com.example.chatapp.ui.feature.signup

import android.net.Uri
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatapp.R
import com.example.chatapp.ui.component.ChatPrimaryButton
import com.example.chatapp.ui.component.ProfilePicture
import com.example.chatapp.ui.component.SecondaryTextField
import com.example.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateWhenAuthorized: () -> Unit,
    navigateWhenSigInClicked: () -> Unit,
) {
    val signUpResultUiState by viewModel.signUpResultUiState.collectAsStateWithLifecycle()
    val formState = viewModel.formState

    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.navigateAfterSigningUpSuccessfully.collectLatest {
            navigateWhenAuthorized()
        }
    }

    SignUpScreen(
        modifier = modifier,
        signUpResultUiState = signUpResultUiState,
        formState = formState,
        onAlertDialogDismiss = { viewModel.resetSignUpResultUiState() },
        onPhotoSelected = {
            viewModel.onEvent(SignUpEvent.ProfilePhotoUriChanged(it))
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
    signUpResultUiState: SignUpViewModel.SignUpResultUiState,
    formState: SignUpState,
    onAlertDialogDismiss: () -> Unit,
    onPhotoSelected: (uri: Uri) -> Unit,
    onFirstNameChanged: (firstName: String) -> Unit,
    onLastNameChanged: (lastName: String) -> Unit,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    onPasswordConfirmationChanged: (password: String) -> Unit,
    signUpClicked: () -> Unit,
    signInClicked: () -> Unit,
) {
    val context = LocalContext.current

    if (signUpResultUiState is SignUpViewModel.SignUpResultUiState.Error) {
        val errorMessageRes = when (signUpResultUiState) {
            SignUpViewModel.SignUpResultUiState.Error.UserWithUsernameAlreadyExists ->
                R.string.error_message_sign_up_user_with_username_already_exists

            SignUpViewModel.SignUpResultUiState.Error.Generic ->
                R.string.common_generic_error_message
        }

        AlertDialog(
            onDismissRequest = onAlertDialogDismiss,
            confirmButton = {
                TextButton(
                    onClick = onAlertDialogDismiss
                ) {
                    Text(text = context.getString(R.string.common_ok))
                }
            },
            title = {
                Text(text = context.getString(R.string.common_generic_error_title))
            },
            text = {
                Text(text = context.getString(errorMessageRes))
            }
        )
    }

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

            SecondaryTextField(
                label = "First name",
                value = formState.firstName,
                errorMessage = formState.firstNameError?.let {
                    context.getString(it, "First name")
                },
                onInputChange = onFirstNameChanged::invoke
            )

            Spacer(modifier = Modifier.height(16.dp))

            SecondaryTextField(
                label = "Last name",
                value = formState.lastName,
                errorMessage = formState.lastNameError?.let {
                    context.getString(it, "Last name")
                },
                onInputChange = onLastNameChanged::invoke
            )

            Spacer(modifier = Modifier.height(16.dp))

            SecondaryTextField(
                label = "E-mail",
                value = formState.email,
                keyboardType = KeyboardType.Email,
                errorMessage = formState.emailError?.let {
                    context.getString(it, "Email")
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
                "as senhas são iguais"
            } else ""

            SecondaryTextField(
                label = "Password",
                value = formState.password,
                extraText = extraText,
                errorMessage = formState.passwordError?.let {
                    context.getString(it)
                },
                keyboardType = KeyboardType.Password
            ) {
                password = it
                onPasswordChanged(it)
            }

            Spacer(modifier = Modifier.height(32.dp))

            SecondaryTextField(
                label = "Password confirmation",
                value = formState.passwordConfirmation,
                extraText = extraText,
                errorMessage = formState.passwordError?.let {
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
                title = "Sign Up",
                modifier = Modifier.fillMaxWidth(),
                isLoading = signUpResultUiState is SignUpViewModel.SignUpResultUiState.Loading,
                onClick = signUpClicked::invoke
            )

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
                signUpResultUiState = SignUpViewModel.SignUpResultUiState.Success,
                formState = SignUpState(),
                {},
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