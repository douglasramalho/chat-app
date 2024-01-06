package com.example.chatapp.data.network.response

import com.example.chatapp.model.Image
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val id: String,
    val name: String,
    val type: String,
    val url: String,
)

fun ImageResponse.toModel() = Image(
    id = this.id,
    name = this.name,
    type = this.type,
    url = this.url,
)
