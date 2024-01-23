package com.example.chatapp.ui.feature.userslist

import com.example.chatapp.model.User

data class UsersListState(
    val isLoading: Boolean = false,
    val usersList: List<User> = emptyList(),
)
