package com.example.chatapp.data.remote.resource

import io.ktor.resources.Resource

object UsersResource {

    @Resource("/users")
    data class Users(
        val offset: String = "0",
        val limit: String = "10"
    ) {

        @Resource("{id}")
        data class Id(val parent: Users = Users(), val id: String)
    }
}