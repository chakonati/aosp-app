package dev.superboring.aosp.chakonati.components.shared

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.window.DialogProperties
import dev.superboring.aosp.chakonati.activities.ui.theme.colors

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    properties: DialogProperties = DialogProperties()
) = androidx.compose.material.AlertDialog(
    onDismissRequest,
    confirmButton,
    modifier,
    dismissButton,
    title,
    text,
    shape,
    backgroundColor = colors().background,
    contentColor = colors().onBackground,
    properties,
)
