package com.example.chatapp.ui.feature.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.R
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.domain.SignInUseCase
import com.example.chatapp.domain.SignUpUseCase
import com.example.chatapp.domain.UploadProfilePictureUseCase
import com.example.chatapp.domain.ValidateEmailFieldUseCase
import com.example.chatapp.domain.ValidateEmptyFieldUseCase
import com.example.chatapp.domain.ValidatePasswordFieldUseCase
import com.example.chatapp.model.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val validateEmptyFieldUseCase: ValidateEmptyFieldUseCase,
    private val validateEmailFieldUseCase: ValidateEmailFieldUseCase,
    private val validatePasswordFieldUseCase: ValidatePasswordFieldUseCase,
) : ViewModel() {

    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.FirstNameChanged -> _signUpUiState.update {
                it.copy(firstName = event.value)
            }

            is SignUpEvent.LastNameChanged -> _signUpUiState.update {
                it.copy(lastName = event.value)
            }

            is SignUpEvent.EmailChanged -> _signUpUiState.update {
                it.copy(email = event.value)
            }

            is SignUpEvent.PasswordChanged -> _signUpUiState.update {
                it.copy(password = event.value)
            }

            is SignUpEvent.PasswordConfirmationChanged -> _signUpUiState.update {
                it.copy(passwordConfirmation = event.value)
            }

            is SignUpEvent.ProfilePhotoPathChanged -> _signUpUiState.update {
                it.copy(profilePhotoPath = event.value)
            }

            SignUpEvent.Submit -> if (isFormValid()) {
                signUp()
            }
        }
    }

    private fun isFormValid(): Boolean {
        val firstNameValidationResult = validateEmptyFieldUseCase(_signUpUiState.value.firstName)
        val lastNameValidationResult = validateEmptyFieldUseCase(_signUpUiState.value.lastName)
        val emailValidationResult = validateEmailFieldUseCase(_signUpUiState.value.email)
        val passwordValidationResult =
            validatePasswordFieldUseCase(
                _signUpUiState.value.password,
                _signUpUiState.value.passwordConfirmation
            )

        _signUpUiState.update {
            it.copy(
                firstNameError = firstNameValidationResult.errorMessage,
                lastNameError = lastNameValidationResult.errorMessage,
                emailError = emailValidationResult.errorMessage,
                passwordError = passwordValidationResult.errorMessage,
            )
        }

        return listOf(
            firstNameValidationResult.successful,
            lastNameValidationResult.successful,
            emailValidationResult.successful,
            passwordValidationResult.successful,
        ).all { it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun signUp() {
        viewModelScope.launch {
            val filePath = _signUpUiState.value.profilePhotoPath
            uploadProfilePictureUseCase(filePath)
                .flatMapLatest { imageResultStatus ->
                    if (imageResultStatus !is ResultStatus.Loading) {
                        signUpUseCase(
                            SignUpUseCase.Params(
                                email = _signUpUiState.value.email,
                                password = _signUpUiState.value.password,
                                firstName = _signUpUiState.value.firstName,
                                lastName = _signUpUiState.value.lastName,
                                profilePictureId = (imageResultStatus as? ResultStatus.Success)?.data
                            )
                        )
                    } else flowOf(imageResultStatus)
                }.flatMapLatest {
                    if (it is ResultStatus.Success) {
                        signInUseCase(
                            SignInUseCase.Params(
                                email = signUpUiState.value.email,
                                password = signUpUiState.value.password,
                            )
                        )
                    } else flowOf(it)
                }.collect { resultStatus ->
                    _signUpUiState.update {
                        it.copy(
                            isLoading = resultStatus is ResultStatus.Loading,
                            isLoggedIn = resultStatus is ResultStatus.Success,
                            errorMessageStringResId = if (resultStatus is ResultStatus.Error) {
                                val error = resultStatus.exception
                                if (error is AppError.ApiError.Conflict) {
                                    R.string.error_message_user_with_username_already_exists
                                } else R.string.common_generic_error_message
                            } else null
                        )
                    }
                }
        }
    }

    fun errorMessageShown() {
        _signUpUiState.update {
            it.copy(
                errorMessageStringResId = null
            )
        }
    }
}