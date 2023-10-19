package com.example.chatapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF00BCCE)
val GreenGrey40 = Color(0xFF4a6266)
val Purple80 = Color(0xFFbac6ea)
val Lumiance95 = Color(0xFFcef8ff)
val Neutral = Color(0xFF8a91a8)
val Surface = Color(0xFFfafdfd)
val InverseSurface = Color(0xFF000000)
val Grey1 = Color(0xFFeff0f1)

val ColorSuccess = Color(0xFF2E9000)
val ColorError = Color(0xFFF24E1E)

val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Primary,
        Color(0xFF1E1E1E),
        Color.Black,
    ),
)