package dev.superboring.aosp.chakonati.extensions.androidx.compose.ui.graphics

import androidx.compose.ui.graphics.Color

fun Color.darken(by: Float) = Color(
    red * (1 - by.coerceAtMost(1f)),
    green * (1 - by.coerceAtMost(1f)),
    blue * (1 - by.coerceAtMost(1f)),
    alpha,
    colorSpace
)

fun Color.lighten(by: Float) = Color(
    red * by.coerceAtLeast(1f),
    green * by.coerceAtLeast(1f),
    blue * by.coerceAtLeast(1f),
    alpha,
    colorSpace
)

fun Color.transparentize(by: Float) = Color(
    red, green, blue,
    alpha * (1 - by.coerceAtMost(1f)),
    colorSpace,
)
