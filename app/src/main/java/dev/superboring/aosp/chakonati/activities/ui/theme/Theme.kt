package dev.superboring.aosp.chakonati.activities.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorPalette = darkColors(
    primary = Blue700,
    primaryVariant = Blue600,
    secondary = Gray900,
    secondaryVariant = Gray700,

    background = Gray900,
    surface = Gray980,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Gray20,
    error = Red400,
    onError = Gray980,
)

val LightColorPalette = lightColors(
    primary = Gray900,
    primaryVariant = Gray700,
    secondary = Blue700,
    secondaryVariant = Blue500,

    background = Gray50,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Gray980,
    error = Red600,
    onError = Gray20,
)

@Composable
fun isInDarkTheme() = isSystemInDarkTheme()

@Composable
fun colors() =
    if (isInDarkTheme()) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

@Composable
fun DefaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = colors(),
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}