package dev.superboring.aosp.chakonati.components.shared.base

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StyledSurface(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(17.dp),
        modifier = Modifier.padding(8.dp),
        content = content,
    )
}