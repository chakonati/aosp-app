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
    modifier: Modifier = Modifier,
    fill: Boolean = false,
    addPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    var mod = modifier
    if (fill) {
        mod = mod.fillMaxSize()
    }
    if (addPadding) {
        mod = mod.padding(16.dp)
    }

    Surface(
        modifier = mod,
        color = colors().background,
        contentColor = colors().onBackground,
        content = content,
    )
}
