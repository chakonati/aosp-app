package dev.superboring.aosp.chakonati.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.activities.ui.theme.colors

@Composable
fun BottomFAB(icon: ImageVector, iconContentDescription: String, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth(),
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(16.dp),
            backgroundColor = colors().primary,
            onClick = onClick
        ) {
            Icon(icon, iconContentDescription)
        }
    }
}