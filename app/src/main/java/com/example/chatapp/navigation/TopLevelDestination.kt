package com.example.chatapp.navigation

import androidx.annotation.DrawableRes
import com.example.chatapp.R

enum class TopLevelDestination(
    val title: String,
    @DrawableRes val icon: Int
) {
    CHATS(icon = R.drawable.ic_bottom_nav_chats, title = "Chats"),
    CALLS(icon = R.drawable.ic_bottom_nav_calls, title = "Calls"),
    PLUS_BUTTON(icon = R.drawable.ic_bottom_nav_calls, title = "Plus button"),
    SEARCH(icon = R.drawable.ic_bottom_nav_search, title = "Search"),
    PROFILE(icon = R.drawable.ic_bottom_nav_profile, title = "Profile"),
}