package dev.superboring.aosp.chakonati.components.fragments.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.components.shared.ResText

@Composable
fun RelayServerSetup() {
    Surface {
        Column {
            TopAppBar(
                title = { ResText(R.string.setup_relay_server__title) }
            )
        }
    }
}