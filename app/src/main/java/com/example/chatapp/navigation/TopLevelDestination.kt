package com.example.chatapp.navigation

import androidx.annotation.DrawableRes
import com.example.chatapp.R

enum class TopLevelDestination(
    val title: String,
    @DrawableRes val icon: Int?
) {
    CHATS(icon = R.drawable.ic_bottom_nav_chats, title = "Chats"),
    PLUS_BUTTON(icon = null, title = "Plus button"),
    PROFILE(icon = R.drawable.ic_bottom_nav_profile, title = "Profile"),
}