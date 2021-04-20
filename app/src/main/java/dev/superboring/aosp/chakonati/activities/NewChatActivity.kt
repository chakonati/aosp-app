package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.shared.BottomFAB
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.components.shared.TextFieldErrorText
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.compose.stringRes
import dev.superboring.aosp.chakonati.domain.ChatSummary
import dev.superboring.aosp.chakonati.extensions.android.view.useTranslucentBars
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.signal.ChatSessionManager
import dev.superboring.aosp.chakonati.x.activity.replaceActivity

class NewChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.useTranslucentBars()

        applyContent()
    }

    private fun applyContent() {
        setContent {
            DefaultTheme {
                BareSurface(addPadding = false) {
                    val coroutineScope = rememberCoroutineScope()

                    var remoteServer by remember { mutableStateOf("") }
                    val isValid = remoteServer.isNotEmpty()
                    var isConnecting by remember { mutableStateOf(false) }
                    var error by remember { mutableStateOf("") }
                    var hasErrorOccurred by remember { mutableStateOf(false) }
                    var failedRemoteServer by remember { mutableStateOf("") }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { ResText(R.string.new_chat__title) }
                            )
                        },
                        bottomBar = {
                            BottomFAB(
                                icon = Icons.Default.ArrowForward,
                                iconContentDescription = R.string.new_chat__start_chat.stringRes(),
                                enabled = isValid && !isConnecting,
                                loading = isConnecting,
                                onClick = {
                                    isConnecting = true
                                    hasErrorOccurred = false
                                    coroutineScope.launchIO {
                                        val chatSession =
                                            ChatSessionManager.chatSession(remoteServer)
                                        try {
                                            val chatSummary = ChatSummary(
                                                chatSession.startNew().id,
                                                remoteServer,
                                                remoteServer,
                                                ""
                                            )
                                            replaceActivity(ChatActivity::class, chatSummary)
                                        } catch (e: Exception) {
                                            failedRemoteServer = remoteServer
                                            error = e.localizedMessage ?: e.message ?: ""
                                            hasErrorOccurred = true
                                            e.printStackTrace()
                                        } finally {
                                            chatSession.disconnect()
                                            isConnecting = false
                                        }
                                    }
                                }
                            )
                        }
                    ) {
                        BareSurface {
                            FullWidthColumn {
                                NewChat(
                                    remoteServer = remoteServer,
                                    enabled = !isConnecting,
                                    onRemoteServerChange = { remoteServer = it },
                                    hasErrorOccurred = hasErrorOccurred,
                                    error = error,
                                    failedRemoteServer = failedRemoteServer,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewChat(
    remoteServer: String,
    enabled: Boolean,
    onRemoteServerChange: (String) -> Unit,
    hasErrorOccurred: Boolean,
    error: String,
    failedRemoteServer: String,
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = remoteServer,
        onValueChange = onRemoteServerChange,
        enabled = enabled,
        label = { ResText(R.string.common__remote_relay_server) },
        placeholder = { Text("chat.person.tld") },
        isError = hasErrorOccurred,
    )
    if (hasErrorOccurred) {
        TextFieldErrorText(
            text = String.format(
                R.string.new_chat__connection_error.stringRes(),
                failedRemoteServer, error
            )
        )
    }
}
