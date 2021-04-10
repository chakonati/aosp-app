package dev.superboring.aosp.chakonati.components.fragments.chat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.additionalColors
import dev.superboring.aosp.chakonati.components.shared.CenteredColumn
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.ResText

val messages = arrayListOf<Message>()

@Composable
fun MessageHistory() {
    Surface(
        color = additionalColors().intermediaryBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        FullWidthColumn(
            modifier = Modifier.padding(8.dp)
        ) {
            if (messages.isNotEmpty()) {
                messages.forEachIndexed { index, message ->
                    MessageRow(message.apply {
                        last = if (index > 0) messages[index - 1] else null
                        next = if (index < messages.size - 1) messages[index + 1] else null
                    })
                }
            } else {
                SendYourFirstMessage()
            }
        }
    }
}

@Composable
@Preview
private fun SendYourFirstMessage() {
    CenteredColumn {
        ResText(
            modifier = Modifier.padding(16.dp),
            stringRes = R.string.chat__send_your_first_message,
            style = typography.h6,
            textAlign = TextAlign.Center,
        )
    }
}
