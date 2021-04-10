package dev.superboring.aosp.chakonati.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.activities.ui.theme.colors

@Composable
fun SmallSnackyBar(
    text: String,
    backgroundColor: Color = colors().primary,
    loading: Boolean = false,
) {
    Surface(color = backgroundColor) {
        FullWidthRow(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 32.dp)
        ) {
            val spinnerWidth = Modifier.size(20.dp)
            if (loading) {
                CircularProgressIndicator(
                    modifier = spinnerWidth,
                    strokeWidth = 2.dp,
                    color = contentColorFor(backgroundColor),
                )
            } else {
                Spacer(modifier = spinnerWidth)
            }
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                color = contentColorFor(backgroundColor),
                text = text,
            )
        }
    }
}
