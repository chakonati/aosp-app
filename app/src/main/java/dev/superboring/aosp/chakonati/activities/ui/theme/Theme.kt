package dev.superboring.aosp.chakonati.activities.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class AdditionalColors(
    val actionBarWindowBackground: Color,
    val intermediaryBackground: Color,
    val success: Color,
)

val DarkColorPalette = darkColors(
    primary = Blue600,
    primaryVariant = Blue700,
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
    secondary = Blue600,
    secondaryVariant = Blue700,

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
fun additionalColors() =
    if (isInDarkTheme()) {
        AdditionalColors(
            actionBarWindowBackground = colors().background,
            intermediaryBackground = Gray950,
            success = Green400,
        )
    } else {
        AdditionalColors(
            actionBarWindowBackground = DarkColorPalette.primary,
            intermediaryBackground = Gray20,
            success = Green600,
        )
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
