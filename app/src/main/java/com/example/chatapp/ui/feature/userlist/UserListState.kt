package com.example.chatapp.ui.feature.userlist

import com.example.chatapp.model.User

data class UserListState(
    val isLoading: Boolean = false,
    val usersList: List<User> = emptyList(),
)
