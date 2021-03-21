package dev.superboring.aosp.chakonati.components.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.activities.ui.theme.additionalColors
import dev.superboring.aosp.chakonati.activities.ui.theme.colors

@Composable
fun ButtonBar(
    startButtonText: @Composable () -> Unit,
    endButtonText: @Composable () -> Unit,
    onStartButtonClick: () -> Unit,
    onEndButtonClick: () -> Unit,
    startButtonDisabled: Boolean = false,
    endButtonDisabled: Boolean = false,
) {
    Surface(color = additionalColors().intermediaryBackground) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(
                    onClick = onStartButtonClick,
                    modifier = Modifier.height(40.dp),
                    enabled = !startButtonDisabled,
                ) {
                    startButtonText()
                }
                TextButton(
                    onClick = onEndButtonClick,
                    modifier = Modifier.height(40.dp),
                    enabled = !endButtonDisabled,
                ) {
                    endButtonText()
                }
            }
        }
    }
}