package com.nlkprojects.kwordle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = White,
    secondary = LightGrey,
    background = DarkGrey,
    primaryVariant = SecondaryWhite,
)

private val LightColorPalette = lightColors(
    primary = Black,
    secondary = DarkGrey,
    background = White,
    primaryVariant = SecondaryBlack,
)

@Composable
fun KWordleTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
