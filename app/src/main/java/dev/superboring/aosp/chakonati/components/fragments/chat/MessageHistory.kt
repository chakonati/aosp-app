package dev.superboring.aosp.chakonati.components.fragments.chat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.additionalColors
import dev.superboring.aosp.chakonati.components.shared.CenteredColumn
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.persistence.dao.lastUpdatedChatId
import dev.superboring.aosp.chakonati.persistence.dao.messageUpdateTracker
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.Chat
import dev.superboring.aosp.chakonati.persistence.entities.DBMessage
import dev.superboring.aosp.chakonati.x.handler.postMain
import kotlinx.coroutines.launch

@Composable
fun MessageHistory(chat: Chat) {
    Surface(
        color = additionalColors().intermediaryBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        val coroutineScope = rememberCoroutineScope()

        var messages by remember { mutableStateOf(null as List<DBMessage>?) }
        var lastUpdateId by remember { mutableStateOf(0L) }

        (messageUpdateTracker[chat.id] ?: 0).let { updateId ->
            if (messages == null ||
                lastUpdatedChatId == chat.id && updateId > lastUpdateId
            ) {
                coroutineScope.launchIO {
                    db.messages().last(chat.id, 100).let {
                        postMain {
                            messages = it
                            lastUpdateId = updateId
                        }
                    }
                }
            }
        }


        messages?.let {
            MessageHistoryMessages(it)
        }
    }
}

@Composable
fun MessageHistoryMessages(descMessages: List<DBMessage>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val messages = descMessages.asReversed()
    if (messages.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            state = listState
        ) {
            itemsIndexed(messages) { index, message ->
                MessageRow(message.asBubbleMessage.apply {
                    last =
                        if (index > 0) messages[index - 1].asBubbleMessage else null
                    next =
                        if (index < messages.size - 1) {
                            messages[index + 1].asBubbleMessage
                        } else null
                })
            }
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    } else {
        CenteredColumn {
            SendYourFirstMessage()
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
