package com.example.chatapp.data.datastore

import kotlinx.serialization.Serializable

@Serializable
data class CurrentUser(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null,
)
