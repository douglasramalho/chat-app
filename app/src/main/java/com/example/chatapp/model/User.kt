package com.example.chatapp.model

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.chatapp.R

data class User(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?
)

fun User.profilePicture() = this.profilePictureUrl ?: R.drawable.no_profile_image

class UserPreviewParameterProvider : PreviewParameterProvider<List<User>> {

    override val values: Sequence<List<User>>
        get() = sequenceOf(
            listOf(
                User(
                    id = "1",
                    username = "will@gmail.com",
                    firstName = "Will",
                    lastName = "Smith",
                    profilePictureUrl = null
                ),
                User(
                    id = "2",
                    username = "jaden@gmail.com",
                    firstName = "Jaden",
                    lastName = "Smith",
                    profilePictureUrl = null
                ),
                User(
                    id = "3",
                    username = "willow@gmail.com",
                    firstName = "Willow",
                    lastName = "Smith",
                    profilePictureUrl = null
                )
            )
        )
}
