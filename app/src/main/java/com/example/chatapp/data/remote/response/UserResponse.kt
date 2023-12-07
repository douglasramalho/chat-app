package com.example.chatapp.data.remote.response

import com.example.chatapp.model.User
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?,
)

fun UserResponse.toModel() = User(
    id = this.id,
    username = this.username,
    firstName = this.firstName,
    lastName = this.lastName,
    profilePictureUrl = this.profilePictureUrl,
)
