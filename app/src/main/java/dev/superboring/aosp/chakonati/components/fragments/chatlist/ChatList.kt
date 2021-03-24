package dev.superboring.aosp.chakonati.components.fragments.chatlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.superboring.aosp.chakonati.domain.ChatSummary

@Composable
fun ChatListView() {
    ChatList(listOf(
        ChatSummary("", "Bob", "Hey, what's up?"),
        ChatSummary("", "Tom", "Okay, see you tomorrow!"),
        ChatSummary("", "Anthony", "Looks good to me"),
        ChatSummary("", "Alice", "Perfect!"),
        ChatSummary("", "Kate", "Does it work?"),
    ))
}

@Composable
private fun ChatList(chats: List<ChatSummary>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(chats) {
            ChatItem(it)
        }
    }
}
