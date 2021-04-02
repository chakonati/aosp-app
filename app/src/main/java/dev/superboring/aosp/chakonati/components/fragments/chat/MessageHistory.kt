package dev.superboring.aosp.chakonati.components.fragments.chat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.activities.ui.theme.additionalColors
import dev.superboring.aosp.chakonati.activities.ui.theme.colors
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.base.StyledSurface

val messages = listOf(
    Message(
        from = MessageFrom.THEM,
        text = "Hi!",
    ),
    Message(
        from = MessageFrom.MYSELF,
        text = "Hey, what's up?",
    ),
    Message(
        from = MessageFrom.THEM,
        text = "Pizza",
    ),
    Message(
        from = MessageFrom.THEM,
        text = "Uhh, no, I meant to say Avocado.",
    )
)

@Composable
fun MessageHistory() {
    Surface(
        color = additionalColors().intermediaryBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        FullWidthColumn(
            modifier = Modifier.padding(8.dp)
        ) {
            messages.forEachIndexed { index, message ->
                MessageRow(message.apply {
                    last = if (index > 0) messages[index - 1] else null
                    next = if (index < messages.size - 1) messages[index + 1] else null
                })
            }
        }
    }
}