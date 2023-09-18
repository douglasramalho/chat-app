package com.example.chatapp.ui.feature.signup

import android.net.Uri
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.chatapp.R
import com.example.chatapp.model.AuthResult
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

    SignUpScreen(
        modifier = modifier,
        formState = state,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpScreen(
    modifier: Modifier,
    formState: SignUpState,
    onPhotoSelected: (uri: Uri) -> Unit,
    onFirstNameChanged: (firstName: String) -> Unit,
    onLastNameChanged: (lastName: String) -> Unit,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    onPasswordConfirmationChanged: (password: String) -> Unit,
    signUpClicked: () -> Unit,
    signInClicked: () -> Unit,
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
            val context = LocalContext.current

            ProfilePicture {
                onPhotoSelected(it)
            }

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
                isLoading = formState.isLoading,
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
                formState = SignUpState(),
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