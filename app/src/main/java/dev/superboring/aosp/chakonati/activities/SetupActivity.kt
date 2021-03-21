package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.fragments.setup.RelayServerSetup
import dev.superboring.aosp.chakonati.components.fragments.setup.WelcomeSetup
import dev.superboring.aosp.chakonati.extensions.android.view.useTranslucentBars

private object Routes {
    const val WELCOME = "welcome"
    const val RELAY_SERVER = "relayServer"
}

class SetupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.useTranslucentBars()

        setContent {
            DefaultTheme {
                Routes()
            }
        }
    }

    @Composable
    private fun Routes() {
        val nav = rememberNavController()
        NavHost(nav, startDestination = Routes.WELCOME) {
            composable(Routes.WELCOME) { WelcomeSetup { nav.navigate(Routes.RELAY_SERVER) } }
            composable(Routes.RELAY_SERVER) { RelayServerSetup() }
        }
    }
}
