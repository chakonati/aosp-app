package dev.superboring.aosp.chakonati.components.shared

import androidx.compose.animation.*
import androidx.compose.runtime.Composable

@ExperimentalAnimationApi
@Composable
fun AnimatedFragment(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = expandIn(),
        exit = shrinkOut(),
        content = content
    )
}