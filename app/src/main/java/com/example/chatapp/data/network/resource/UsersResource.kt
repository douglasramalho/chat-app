package com.example.chatapp.data.network.resource

import io.ktor.resources.Resource

object UsersResource {

    @Resource("/users")
    data class Users(
        val offset: String = "0",
        val limit: String = "10"
    )

    @Resource("/users/{id}")
    data class ById(val id: String)
}