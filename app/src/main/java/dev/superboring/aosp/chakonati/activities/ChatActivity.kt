package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.common.ConnectionBar
import dev.superboring.aosp.chakonati.components.fragments.chat.ComposeMessage
import dev.superboring.aosp.chakonati.components.fragments.chat.MessageHistory
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.domain.ChatSummary
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.services.RemoteMessaging
import dev.superboring.aosp.chakonati.signal.ChatSession
import dev.superboring.aosp.chakonati.x.activity.parameters
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
    val chatSession by remember { mutableStateOf(ChatSession(chatSummary.recipient)) }

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
                    }
                )
            }
        ) {
            MessageHistory()
        }
    }
}
