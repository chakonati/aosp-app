package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.fragments.setup.WelcomeSetup
import dev.superboring.aosp.chakonati.extensions.android.view.useTranslucentBars
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.x.activity.replaceActivity

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.useTranslucentBars()

        setContent {
            val coroutineScope = rememberCoroutineScope()

            DefaultTheme {
                WelcomeSetup(
                    onNext = {
                        replaceActivity(SetupActivity::class)
                    },
                    finishSetup = {
                        coroutineScope.launchIO {
                            finishSetup()
                        }
                    }
                )
            }
        }
    }
}

