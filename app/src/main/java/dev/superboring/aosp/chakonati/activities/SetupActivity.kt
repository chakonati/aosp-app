package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.fragments.setup.RelayServerSetup
import dev.superboring.aosp.chakonati.extensions.android.view.useTranslucentBars
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.dao.save
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.x.activity.replaceActivity
import dev.superboring.aosp.chakonati.x.toast.showToast

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
        val coroutineScope = rememberCoroutineScope()
        NavHost(
            nav,
            startDestination = Routes.RELAY_SERVER
        ) {
            composable(Routes.RELAY_SERVER) {
                RelayServerSetup(
                    onPrevClick = { nav.navigate(Routes.WELCOME) },
                    onNextClick = { coroutineScope.launchIO { finishSetup() } },
                )
            }
        }
    }
}

suspend fun ComponentActivity.finishSetup() {
    // We're done!
    db.mySetup().get().apply { isSetUp = true }.save()

    showToast(R.string.setup_relay_server__setup_finished)
    replaceActivity(MainActivity::class)
}
