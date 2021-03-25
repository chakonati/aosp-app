package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.room.Room
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.fragments.chatlist.ChatListView
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.extensions.android.view.useTranslucentBars
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.persistence.AppDatabase
import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.service.prepareOwnRelayCommunicator
import dev.superboring.aosp.chakonati.signal.OneTimePreKeyRefresh
import dev.superboring.aosp.chakonati.x.activity.replaceActivity
import dev.superboring.aosp.chakonati.x.handler.postMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.concurrent.timer
import kotlin.coroutines.CoroutineContext

class ChatListActivity : ComponentActivity(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.useTranslucentBars()

        prepareDB()

        launchIO {
            if (db.mySetup().get().isSetUp) {
                postMain {
                    applyContent()
                }
                if (db.mySetup().get().relayServer.isNotEmpty()) {
                    prepareOwnRelayCommunicator()
                    OneTimePreKeyRefresh.refreshOneTimePreKeys()
                }
            } else {
                replaceActivity(WelcomeActivity::class)
            }
        }
    }

    private fun applyContent() {
        setContent {
            DefaultTheme {
                BareSurface(addPadding = false) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(getString(R.string.app_name)) }
                            )
                        }
                    ) {
                        ChatListView()
                    }
                }
            }
        }
    }

    private fun prepareDB() {
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "main"
        ).build()
    }
}
