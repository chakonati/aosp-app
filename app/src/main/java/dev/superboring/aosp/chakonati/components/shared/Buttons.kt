package dev.superboring.aosp.chakonati.components.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.activities.ui.theme.colors
import dev.superboring.aosp.chakonati.extensions.androidx.compose.ui.graphics.darken
import dev.superboring.aosp.chakonati.extensions.androidx.compose.ui.graphics.transparentize

private val FabSize = 56.dp

@Composable
fun BottomFAB(
    icon: ImageVector,
    iconContentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth(),
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(16.dp),
            enabled = enabled,
            backgroundColor = if (enabled) colors().primary else colors().primary.darken(.5f),
            onClick = if (enabled) onClick else ({})
        ) {
            if (!loading) {
                Icon(
                    icon,
                    iconContentDescription,
                    tint = if (enabled) {
                        colors().onPrimary
                    } else {
                        colors().onPrimary.transparentize(.6f)
                    }
                )
            } else {
                CircularProgressIndicator(
                    color = if (enabled) {
                        colors().onPrimary
                    } else {
                        colors().onPrimary.transparentize(.6f)
                    },
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    backgroundColor: Color = MaterialTheme.colors.secondary,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.let {
            if (enabled) {
                it.clickable(
                    onClick = onClick,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = null
                )
            } else it
        },
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation.elevation(interactionSource).value
    ) {
        CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(MaterialTheme.typography.button) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = FabSize, minHeight = FabSize)
                        .indication(interactionSource, rememberRipple()),
                    contentAlignment = Alignment.Center
                ) { content() }
            }
        }
    }
}



@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: ButtonElevation = ButtonDefaults.elevation(),
    icon: ImageVector,
    iconContentDescription: String,
) {
    Surface(
        modifier = modifier.let {
            if (enabled) {
                it.clickable(
                    onClick = onClick,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = null
                )
            } else it
        },
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation.elevation(enabled, interactionSource).value
    ) {
        CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(MaterialTheme.typography.button) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = FabSize, minHeight = FabSize)
                        .indication(interactionSource, rememberRipple()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        iconContentDescription,
                        tint = if (enabled) {
                            colors().onPrimary
                        } else {
                            colors().onPrimary.transparentize(.6f)
                        }
                    )
                }
            }
        }
    }
}
