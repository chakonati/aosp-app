package dev.superboring.aosp.chakonati.components.fragments.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
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
import dev.superboring.aosp.chakonati.service.ownRelayCommunicator
import dev.superboring.aosp.chakonati.services.Setup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RelayServerSetup(
    onPrevClick: () -> Unit, onNextClick: () -> Unit,
    onNoWayBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isVerifying by remember { mutableStateOf(false) }
    var relayServer by remember { mutableStateOf("") }
    var failedRelayServer by remember { mutableStateOf("") }
    var verificationFailed by remember { mutableStateOf(false) }
    var verificationSucceeded by remember { mutableStateOf(false) }
    val verificationFinished = verificationFailed || verificationSucceeded
    var failureReason by remember { mutableStateOf(null as String?) }
    val isValid = relayServer.isNotEmpty()

    var hasCheckedPassword by remember { mutableStateOf(false) }
    var isPasswordAlreadySet by remember { mutableStateOf(false) }
    var setupPassword by remember { mutableStateOf("") }

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
                                onNoWayBack()
                                isVerifying = true
                                coroutineScope.launch(Dispatchers.IO) {
                                    ownRelayCommunicator = Communicator(relayServer)
                                    val result = verifyRelayServer { password, isSet ->
                                        setupPassword = password
                                        isPasswordAlreadySet = isSet
                                        hasCheckedPassword = true
                                    }
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
                        startButtonDisabled = isVerifying || verificationFinished,
                        endButtonDisabled = !isValid || isVerifying,
                    )
                }
            ) {
                BareSurface(fill = true) {
                    when {
                        verificationFailed || !verificationSucceeded && !isVerifying ->
                            EditForm(
                                relayServer = relayServer,
                                onRelayServerChange = { relayServer = it },
                                verificationFailed = verificationFailed,
                                failureReason = failureReason,
                                failedRelayServer = failedRelayServer,
                            )
                        isVerifying ->
                            Verification(
                                relayServer = relayServer
                            )
                        hasCheckedPassword ->
                            PasswordSetup(setupPassword, isPasswordAlreadySet)
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

private suspend fun verifyRelayServer(
    onPassword: (String, Boolean) -> Unit
): Pair<Boolean, String?> {
    return try {
        ownRelayCommunicator.doHandshake()
        val isPasswordSet = Setup.isPasswordSet()
        onPassword(if (!isPasswordSet) Setup.setPassword() else "", isPasswordSet)
        Pair(true, null)
    } catch (e: Exception) {
        Pair(false, e.localizedMessage)
    }
}

@Composable
private fun PasswordSetup(password: String, isPasswordAlreadySet: Boolean) {
    Column {
        when {
            isPasswordAlreadySet -> EnterSetupPassword()
            else -> NewSetupPassword(password)
        }
    }
}

@Composable
private fun NewSetupPassword(password: String) {
    ResText(R.string.setup_relay_server__password_desc_new)
    Spacer(Modifier.height(16.dp))
    Text(
        fontFamily = FontFamily.Monospace,
        fontSize = 18.sp,
        text = password
    )
    Spacer(Modifier.height(1.dp))
    ResText(
        stringRes = R.string.setup_relay_server__tap_on_pw_to_copy,
        style = typography.caption,
    )
    Spacer(Modifier.height(16.dp))
    ResText(R.string.setup_relay_server__keep_password_safe)
}

@Composable
private fun EnterSetupPassword() {
    ResText(R.string.setup_relay_server__password_desc_pw_exists)
    Spacer(Modifier.height(16.dp))
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = "",
        label = { ResText(R.string.common__password) },
        placeholder = { ResText(R.string.setup_relay_server__password_placeholder) },
        onValueChange = { },
    )
}