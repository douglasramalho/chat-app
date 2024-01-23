package com.example.chatapp.data.network.resource

import io.ktor.resources.Resource

object MessagesResource {
    @Resource("/messages/{receiverId}")
    data class Messages(
        val receiverId: String,
        val offset: String = "0",
        val limit: String = "30"
    )
}