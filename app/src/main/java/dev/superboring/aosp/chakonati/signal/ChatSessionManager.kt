package dev.superboring.aosp.chakonati.signal

import com.google.common.collect.HashBasedTable
import dev.superboring.aosp.chakonati.components.fragments.chat.Message
import dev.superboring.aosp.chakonati.components.fragments.chat.MessageFrom
import dev.superboring.aosp.chakonati.domain.MessageNotification
import dev.superboring.aosp.chakonati.extras.msgpack.deserialize
import dev.superboring.aosp.chakonati.persistence.dao.add
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.Chat
import dev.superboring.aosp.chakonati.service.OwnRelayServer
import dev.superboring.aosp.chakonati.service.SubscriptionListener
import dev.superboring.aosp.chakonati.services.Subscription
import dev.superboring.aosp.chakonati.services.Subscriptions
import dev.superboring.aosp.chakonati.x.logging.logDebug
import kotlinx.coroutines.FlowPreview
import java.nio.charset.StandardCharsets
import java.util.*

object ChatSessionManager : SubscriptionListener {

    val chatSessions = HashBasedTable.create<String, Int, ChatSession>()

    init {
        OwnRelayServer.comm.apply {
            setSubscriptionListener(Subscriptions.MESSAGES, this@ChatSessionManager)
        }
    }

    suspend fun subscribe() {
        Subscription.subscribe(Subscriptions.MESSAGES)
    }

    override suspend fun onNotification(bytes: ByteArray) {
        val messageNotification = bytes.deserialize<MessageNotification>()
        chatSession(messageNotification.from)
            .onNotification(messageNotification)
    }

    fun chatSession(remoteServer: String): ChatSession = synchronized(this) {
        // TODO: handle device IDs?
        val deviceId = 0
        return chatSessions[remoteServer, deviceId] ?: run {
            val chatSession = ChatSession(remoteServer)
            if (db.signalSessions().hasSession(remoteServer)) {
                val addressId = db.remoteAddresses().get(remoteServer).Id
                chatSession.useChat(db.chats().getByRemoteAddressId(addressId))
            }
            chatSessions.put(remoteServer, deviceId, chatSession)
            chatSession
        }
    }

    fun restoreSessions() {
        logDebug("Restoring chat sessions")
        db.chats().allFull().forEach { chat ->
            logDebug("Restoring ${chat.id} – ${chat.remoteAddress.address}")
            startListening(chat)
        }

    }

    fun startListening(chat: Chat) {
        startListeningOn(chatSession(chat.remoteAddress.address))
        logDebug("Listening to messages for ${chat.remoteAddressId} – ${chat.id}")
    }

    fun startListeningOn(chatSession: ChatSession) {
        chatSession.apply {
            listen {
                onMessage {
                    db.messages().add(
                        Message(
                            MessageFrom.THEM,
                            String(decrypt(it), StandardCharsets.UTF_8)
                        ).asDBMessage(chat)
                    )
                }
            }
        }
    }

}