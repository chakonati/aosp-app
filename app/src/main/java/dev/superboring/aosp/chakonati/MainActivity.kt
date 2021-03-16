package dev.superboring.aosp.chakonati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import dev.superboring.aosp.chakonati.protocol.requests.EchoRequest
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.ui.theme.DefaultTheme

class MainActivity : ComponentActivity() {
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

        Thread {
            val communicator = Communicator("192.168.2.110:4560")
            communicator send EchoRequest("test value yes")
        }.start()
    }
}
