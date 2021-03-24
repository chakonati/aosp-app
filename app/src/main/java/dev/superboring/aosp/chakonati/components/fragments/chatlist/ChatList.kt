package dev.superboring.aosp.chakonati.components.fragments.chatlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import dev.superboring.aosp.chakonati.domain.ChatSummary
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.Chat

@Composable
fun ChatListView() {
    val pager = remember {
        Pager(
            PagingConfig(
                pageSize = 40,
                enablePlaceholders = true,
                maxSize = 1000
            ),
            pagingSourceFactory = db.chats().all().asPagingSourceFactory()
        )
    }

    ChatList(pager)
}

@Composable
private fun ChatList(chats: Pager<Int, Chat>) {
    val lazyItems = chats.flow.collectAsLazyPagingItems()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(lazyItems) {
            ChatItem(it?.summary ?: ChatSummary("", "", ""))
        }
    }
}
