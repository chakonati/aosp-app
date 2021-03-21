package dev.superboring.aosp.chakonati.components.shared.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Inner(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.padding(8.dp),
        content = content,
    )
}
