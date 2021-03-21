package dev.superboring.aosp.chakonati.components.fragments.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.additionalColors
import dev.superboring.aosp.chakonati.activities.ui.theme.colors
import dev.superboring.aosp.chakonati.components.shared.ButtonBar
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.compose.stringRes
import dev.superboring.aosp.chakonati.service.Communicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RelayServerSetup(
    onPrevClick: () -> Unit, onNextClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isVerifying by remember { mutableStateOf(false) }
    var relayServer by remember { mutableStateOf("") }
    var failedRelayServer by remember { mutableStateOf("") }
    var verificationFailed by remember { mutableStateOf(false) }
    var verificationSucceeded by remember { mutableStateOf(false) }
    var failureReason by remember { mutableStateOf(null as String?) }
    val isValid = relayServer.isNotEmpty()

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
                        onStartButtonClick = onPrevClick,
                        onEndButtonClick = {
                            verificationFailed = false
                            failureReason = null
                            failedRelayServer = ""
                            if (!isVerifying) {
                                isVerifying = true
                                coroutineScope.launch(Dispatchers.IO) {
                                    val result = verifyRelayServer(relayServer)
                                    failureReason = result.second
                                    failedRelayServer = relayServer
                                    verificationFailed = !result.first
                                    verificationSucceeded = result.first
                                    isVerifying = false
                                }
                            } else {
                                onNextClick()
                            }
                        },
                        startButtonDisabled = isVerifying,
                        endButtonDisabled = isVerifying || !isValid || verificationSucceeded,
                    )
                }
            ) {
                BareSurface(fill = true) {
                    if (!isVerifying) {
                        EditForm(
                            relayServer = relayServer,
                            onRelayServerChange = { relayServer = it },
                            verificationFailed = verificationFailed,
                            failureReason = failureReason,
                            failedRelayServer = failedRelayServer,
                        )
                    } else {
                        Verification(
                            relayServer = relayServer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditForm(
    onRelayServerChange: (server: String) -> Unit,
    relayServer: String,
    verificationFailed: Boolean,
    failureReason: String? = null,
    failedRelayServer: String = "",
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            ResText(R.string.setup_relay_server__enter_address_desc)

            Spacer(Modifier.height(8.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = relayServer,
                onValueChange = { onRelayServerChange(it) },
                label = { ResText(R.string.common__relay_server_address) },
                placeholder = { Text("chat.example.tld") },
                isError = verificationFailed,
            )
            if (verificationFailed) {
                Spacer(Modifier.height(1.dp))
                Text(
                    text = String.format(
                        R.string.setup_relay_server__connection_failed.stringRes(),
                        failedRelayServer,
                        failureReason!!
                    ),
                    fontSize = 12.sp,
                    color = colors().error,
                )
            }
        }
    }
}

@Composable
private fun Verification(
    relayServer: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        Text(
            textAlign = TextAlign.Center,
            text = String.format(
                R.string.setup_relay_server__connecting_to_relay_server.stringRes(),
                relayServer
            )
        )
    }
}

private suspend fun verifyRelayServer(relayServer: String): Pair<Boolean, String?> {
    return try {
        Communicator(relayServer).apply {
            doHandshake()
        }.disconnect()
        Pair(true, null)
    } catch (e: Exception) {
        Pair(false, e.localizedMessage)
    }
}