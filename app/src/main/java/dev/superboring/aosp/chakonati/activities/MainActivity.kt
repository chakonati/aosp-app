package dev.superboring.aosp.chakonati.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.room.Room
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.persistence.AppDatabase
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.extensions.android.view.useTranslucentBars
import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.service.prepareOwnRelayCommunicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : ComponentActivity(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.useTranslucentBars()

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "main"
        ).build()

        launch(Dispatchers.IO) {
            if (db.mySetup().get().isSetUp) {
                applyContent()
                prepareOwnRelayCommunicator()
            } else {
                startActivity(
                    Intent(this@MainActivity, SetupActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_TASK_ON_HOME
                    }
                )
                finish()
            }
        }
    }

    private fun applyContent() {
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
    }
}
