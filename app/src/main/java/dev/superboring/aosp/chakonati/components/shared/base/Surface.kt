package dev.superboring.aosp.chakonati.components.shared.base

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.activities.ui.theme.colors

@Composable
fun StyledSurface(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(17.dp),
        modifier = Modifier.padding(8.dp),
        content = content,
    )
}

@Composable
fun BareSurface(
    fill: Boolean = false,
    addPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    var modifier: Modifier = Modifier
    if (fill) {
        modifier = modifier.fillMaxSize()
    }
    if (addPadding) {
        modifier = modifier.padding(16.dp)
    }

    Surface(
        modifier = modifier,
        color = colors().background,
        contentColor = colors().onBackground,
        content = content,
    )
}