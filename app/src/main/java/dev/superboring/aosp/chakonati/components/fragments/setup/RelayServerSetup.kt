package dev.superboring.aosp.chakonati.components.fragments.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.additionalColors
import dev.superboring.aosp.chakonati.components.shared.ButtonBar
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface

@Composable
fun RelayServerSetup() {
    Surface(color = additionalColors().actionBarWindowBackground) {
        BareSurface(addPadding = false) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { ResText(R.string.setup_relay_server__title) }
                    )
                },
                bottomBar = {
                    ButtonBar(
                        startButtonText = { ResText(R.string.common__back) },
                        endButtonText = { ResText(R.string.common__next) },
                        onStartButtonClick = {},
                        onEndButtonClick = {},
                    )
                }
            ) {
                BareSurface(fill = true) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column {
                            var text by remember { mutableStateOf("") }

                            ResText(R.string.setup_relay_server__enter_address_desc)

                            Spacer(Modifier.height(8.dp))

                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = text,
                                onValueChange = { text = it },
                                label = { ResText(R.string.common__relay_server_address) },
                                placeholder = { Text("chat.example.tld") }
                            )
                        }
                    }
                }
            }
        }
    }
}