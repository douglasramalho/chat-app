package com.example.chatapp.model

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

data class Image(
    val id: String,
    val name: String,
    val type: String,
    val url: String
)

class ImagePreviewParameterProvider : PreviewParameterProvider<Image> {

    override val values: Sequence<Image>
        get() = sequenceOf(Image(
            id = "1",
            name = "profile",
            type = "jpeg",
            url = "url"
        ))
}
