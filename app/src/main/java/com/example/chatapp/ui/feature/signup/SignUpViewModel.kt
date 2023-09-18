package com.example.chatapp.ui.feature.signup

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repository.AuthRepository
import com.example.chatapp.domain.ValidateEmailFieldUseCase
import com.example.chatapp.domain.ValidateEmptyFieldUseCase
import com.example.chatapp.domain.ValidatePasswordFieldUseCase
import com.example.chatapp.mediastorage.MediaStorageHelper
import com.example.chatapp.model.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    init {
        // authenticate()
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

        val passwordValidationResult = validatePasswordFieldUseCase(formState.password, formState.passwordConfirmation)
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
            formState = formState.copy(isLoading = true)
            val result = authRepository.signUp(
                username = formState.email,
                password = formState.password,
                firstName = formState.firstName,
                lastName = formState.lastName,
                profilePictureUrl = null
            )
            resultChannel.send(result)

            formState = formState.copy(isLoading = false)
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
            formState = formState.copy(isLoading = true)
            val result = authRepository.authenticate()
            resultChannel.send(result)
            formState = formState.copy(isLoading = false)
        }
    }
}