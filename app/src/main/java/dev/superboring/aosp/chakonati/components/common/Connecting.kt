package dev.superboring.aosp.chakonati.components.common

import androidx.compose.runtime.Composable
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.additionalColors
import dev.superboring.aosp.chakonati.activities.ui.theme.colors
import dev.superboring.aosp.chakonati.components.shared.SmallSnackyBar
import dev.superboring.aosp.chakonati.compose.stringRes
import dev.superboring.aosp.chakonati.persistence.dao.isRelayServerSetUp
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.service.OwnRelayServer

@Composable
fun ConnectionBar() {
    if (db.mySetup().isRelayServerSetUp) {
        OwnRelayServer.run {
            when {
                isConnecting -> {
                    ConnectingBar()
                }
                connectionError != null -> {
                    connectionError?.localizedMessage?.let { ConnectionErrorBar(it) }
                }
                justConnectedSuccessfully -> {
                    ConnectedSuccessfully()
                }
                else -> {
                }
            }
        }
    }
}

@Composable
private fun ConnectingBar() {
    SmallSnackyBar(text = R.string.common__connecting.stringRes(), loading = true)
}

@Composable
private fun ConnectionErrorBar(error: String) {
    SmallSnackyBar(
        text = String.format(
            R.string.common__connection_error_relay_server.stringRes(),
            error
        ),
        backgroundColor = colors().error
    )
}

@Composable
private fun ConnectedSuccessfully() {
    SmallSnackyBar(
        text = R.string.common__connected.stringRes(),
        backgroundColor = additionalColors().success,
    )
}
