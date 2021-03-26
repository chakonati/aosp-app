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
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.components.shared.base.StyledSurface
import dev.superboring.aosp.chakonati.compose.stringRes
import dev.superboring.aosp.chakonati.extensions.android.view.useTranslucentBars

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
                                onClick = {}
                            )
                        }
                    ) {
                        BareSurface {
                            FullWidthColumn {
                                NewChat()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewChat() {
    var remoteServer by remember { mutableStateOf("") }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = remoteServer,
        onValueChange = { remoteServer = it },
        label = { ResText(R.string.common__remote_relay_server) },
        placeholder = { Text("chat.person.tld") },
    )
}