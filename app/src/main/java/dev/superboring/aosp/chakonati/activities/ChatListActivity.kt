package dev.superboring.aosp.chakonati.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.room.Room
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.DefaultTheme
import dev.superboring.aosp.chakonati.components.common.ConnectionBar
import dev.superboring.aosp.chakonati.components.fragments.chatlist.ChatListView
import dev.superboring.aosp.chakonati.components.shared.BottomFAB
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.compose.currentContext
import dev.superboring.aosp.chakonati.compose.stringRes
import dev.superboring.aosp.chakonati.extensions.android.view.useTranslucentBars
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.persistence.AppDatabase
import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.dao.isRelayServerSetUp
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.service.OwnRelayServer
import dev.superboring.aosp.chakonati.signal.ChatSessionManager
import dev.superboring.aosp.chakonati.signal.OneTimePreKeyRefresh
import dev.superboring.aosp.chakonati.x.activity.launchActivity
import dev.superboring.aosp.chakonati.x.activity.replaceActivity
import dev.superboring.aosp.chakonati.x.handler.postMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
                    db.mySetup().isRelayServerSetUp = true
                    OwnRelayServer.prepareCommunicator()
                    OneTimePreKeyRefresh.refreshOneTimePreKeys()

                    ChatSessionManager.restoreSessions()
                    ChatSessionManager.subscribe()
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
                            FullWidthColumn {
                                TopAppBar(
                                    title = { ResText(R.string.app_name) }
                                )
                                ConnectionBar()
                            }
                        },
                        bottomBar = {
                            BottomBar()
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

@Composable
private fun BottomBar(): Unit = currentContext {
    BottomFAB(
        icon = Icons.Default.Add,
        iconContentDescription = R.string.chat_list__start_new_chat.stringRes(),
        onClick = { launchActivity(NewChatActivity::class) },
    )
}
