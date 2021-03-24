package dev.superboring.aosp.chakonati.components.fragments.chatlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.FullWidthRow
import dev.superboring.aosp.chakonati.domain.ChatSummary

@Composable
fun ChatItem(chat: ChatSummary) {
    FullWidthRow(Modifier.clickable {  }) {
        FullWidthColumn {
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = chat.displayName,
                    style = typography.body1,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = chat.lastMessage,
                    style = typography.caption,
                )
            }
            Divider()
        }
    }
}