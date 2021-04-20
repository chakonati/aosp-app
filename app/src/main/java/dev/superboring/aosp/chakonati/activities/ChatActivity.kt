package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.common.ConnectionBar
import dev.superboring.aosp.chakonati.components.fragments.chat.ComposeMessage
import dev.superboring.aosp.chakonati.components.fragments.chat.Message
import dev.superboring.aosp.chakonati.components.fragments.chat.MessageFrom
import dev.superboring.aosp.chakonati.components.fragments.chat.MessageHistory
import dev.superboring.aosp.chakonati.components.shared.FillingBox
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.domain.ChatSummary
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.persistence.dao.add
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.Chat
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.services.RemoteMessaging
import dev.superboring.aosp.chakonati.signal.ChatSessionManager
import dev.superboring.aosp.chakonati.x.activity.parameters
import dev.superboring.aosp.chakonati.x.handler.postMain
import java.nio.charset.StandardCharsets

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultTheme {
                Content(parameters()!!)
            }
        }
    }
}

@Composable
private fun Content(chatSummary: ChatSummary) {
    val coroutineScope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    var chat by remember { mutableStateOf(null as Chat?) }

    coroutineScope.launchIO {
        val dbChat = db.chats() get chatSummary.chatId
        postMain {
            chat = dbChat
        }
    }

    if (chat == null) {
        return
    }

    val chatSession by remember {
        mutableStateOf(
            ChatSessionManager.chatSession(chatSummary.recipient).apply {
                useChat(chat!!)
                coroutineScope.launchIO {
                    listen {
                        onMessage {
                            db.messages() add (
                                Message(
                                    MessageFrom.THEM,
                                    String(decrypt(it), StandardCharsets.UTF_8)
                                ).asDBMessage(chat!!)
                            )
                        }
                    }
                }
            }
        )
    }

    BareSurface(addPadding = false) {
        Scaffold(
            topBar = {
                FullWidthColumn {
                    TopAppBar(
                        title = { Text(chatSummary.displayName) }
                    )
                    ConnectionBar()
                }
            },
            bottomBar = {
                ComposeMessage(
                    onSend = { message ->
                        coroutineScope.launchIO {
                            db.messages() add Message(
                                MessageFrom.MYSELF,
                                message
                            ).asDBMessage(chat!!)
                            postMain {
                                text = ""
                            }

                            Communicator(chatSummary.recipient) transaction {
                                RemoteMessaging(this).run {
                                    sendMessage(
                                        chatSession.useExisting {
                                            encrypt(message.toByteArray(StandardCharsets.UTF_8))
                                        }
                                    )
                                }
                            }
                        }
                    },
                    onTextChange = { text = it },
                    text = text,
                )
            }
        ) {
            FillingBox(modifier = Modifier.padding(it)) {
                MessageHistory(chat!!)
            }
        }
    }
}
