package com.example.chatapp.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.util.ResultStatus
import com.example.chatapp.domain.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val logoutChannel = Channel<Unit>()
    val logoutResult = logoutChannel.receiveAsFlow()

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().collect {
                if (it is ResultStatus.Success) {
                    logoutChannel.send(Unit)
                }
            }
        }
    }
}