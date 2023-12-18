package com.example.chatapp.data.remote.resource

import io.ktor.resources.Resource

object ConversationsResource {
    @Resource("/conversations")
    data class GetPaginated(
        val offset: String = "0",
        val limit: String = "10"
    )
}
