package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.domain.ChatSummary
import dev.superboring.aosp.chakonati.x.activity.parameters

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
    BareSurface(addPadding = false) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(chatSummary.displayName) }
                )
            },
            bottomBar = {}
        ) {

        }
    }
}