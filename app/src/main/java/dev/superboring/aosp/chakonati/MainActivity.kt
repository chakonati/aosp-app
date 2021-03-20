package dev.superboring.aosp.chakonati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.services.GlobalBasics
import dev.superboring.aosp.chakonati.ui.theme.DefaultTheme
import kotlinx.coroutines.*
import dev.superboring.aosp.chakonati.signal.signalExample
import kotlin.coroutines.CoroutineContext

class MainActivity : ComponentActivity(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        TopAppBar(
                            title = { Text(getString(R.string.app_name)) }
                        )
                    }
                }
            }
        }

        launch(Dispatchers.IO) {
            ownRelayCommunicator = Communicator("192.168.2.110:4560")
            println(GlobalBasics.echo("what's up, server :)"))
            signalExample()
        }
    }
}
