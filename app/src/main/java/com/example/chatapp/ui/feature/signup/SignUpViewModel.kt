package com.example.chatapp.ui.feature.signup

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.model.Result
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.domain.ValidateEmailFieldUseCase
import com.example.chatapp.domain.ValidateEmptyFieldUseCase
import com.example.chatapp.domain.ValidatePasswordFieldUseCase
import com.example.chatapp.mediastorage.MediaStorageHelper
import com.example.chatapp.model.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mediaStorageHelper: MediaStorageHelper,
    private val validateEmptyFieldUseCase: ValidateEmptyFieldUseCase,
    private val validateEmailFieldUseCase: ValidateEmailFieldUseCase,
    private val validatePasswordFieldUseCase: ValidatePasswordFieldUseCase,
) : ViewModel() {

    var formState by mutableStateOf(SignUpState())

    var profilePictureUri by mutableStateOf<Uri?>(null)

    private val _signUpResultUiState =
        MutableStateFlow<SignUpResultUiState>(SignUpResultUiState.Success)
    val signUpResultUiState = _signUpResultUiState.asStateFlow()

    private val _navigateAfterSigningUpSuccessfully = Channel<Unit>()
    val navigateAfterSigningUpSuccessfully = _navigateAfterSigningUpSuccessfully.receiveAsFlow()

    init {
        authenticate()
    }

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.FirstNameChanged -> {
                formState = formState.copy(firstName = event.value)
            }

            is SignUpEvent.LastNameChanged -> {
                formState = formState.copy(lastName = event.value)
            }

            is SignUpEvent.EmailChanged -> {
                formState = formState.copy(email = event.value)
            }

            is SignUpEvent.PasswordChanged -> {
                formState = formState.copy(password = event.value)
            }

            is SignUpEvent.PasswordConfirmationChanged -> {
                formState = formState.copy(passwordConfirmation = event.value)
            }

            is SignUpEvent.ProfilePhotoUriChanged -> {
                formState = formState.copy(profilePhotoUri = event.value)
            }

            SignUpEvent.Submit -> {
                if (isFormValid()) {
                    uploadProfilePicture()
                    signUp()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        val firstNameValidationResult = validateEmptyFieldUseCase(formState.firstName)
        formState = formState.copy(firstNameError = firstNameValidationResult.errorMessage)

        val lastNameValidationResult = validateEmptyFieldUseCase(formState.lastName)
        formState = formState.copy(lastNameError = lastNameValidationResult.errorMessage)

        val emailValidationResult = validateEmailFieldUseCase(formState.email)
        formState = formState.copy(emailError = emailValidationResult.errorMessage)

        val passwordValidationResult =
            validatePasswordFieldUseCase(formState.password, formState.passwordConfirmation)
        formState = formState.copy(passwordError = passwordValidationResult.errorMessage)

        return listOf(
            firstNameValidationResult.successful,
            lastNameValidationResult.successful,
            emailValidationResult.successful,
            passwordValidationResult.successful,
        ).all { it }
    }

    private fun signUp() {
        viewModelScope.launch {
            authRepository.signUp(
                username = formState.email,
                password = formState.password,
                firstName = formState.firstName,
                lastName = formState.lastName,
                profilePictureUrl = null
            ).collect { result ->
                _signUpResultUiState.value = when (result) {
                    Result.Loading -> SignUpResultUiState.Loading

                    is Result.Success -> {
                        _navigateAfterSigningUpSuccessfully.send(Unit)
                        SignUpResultUiState.Success
                    }

                    is Result.Error -> when (result.exception) {
                        AppError.ApiError.Conflict -> SignUpResultUiState.Error.UserWithUsernameAlreadyExists
                        else -> SignUpResultUiState.Error.Generic
                    }
                }
            }
        }
    }

    fun onProfilePictureSelected(uri: Uri) {
        profilePictureUri = uri
    }

    fun uploadProfilePicture() {
        profilePictureUri?.let {
            mediaStorageHelper.uploadImage("profilePicture", it)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            val result = authRepository.authenticate()
            if (result is Result.Success) {
                _navigateAfterSigningUpSuccessfully.send(Unit)
            }
        }
    }

    fun resetSignUpResultUiState() {
        _signUpResultUiState.value = SignUpResultUiState.Success
    }

    sealed interface SignUpResultUiState {
        data object Loading : SignUpResultUiState
        sealed interface Error : SignUpResultUiState {
            data object UserWithUsernameAlreadyExists : Error
            data object Generic : Error
        }

        data object Success : SignUpResultUiState
    }
}