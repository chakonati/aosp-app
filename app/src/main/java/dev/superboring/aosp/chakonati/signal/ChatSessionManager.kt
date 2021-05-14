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
import dev.superboring.aosp.chakonati.services.Messaging
import dev.superboring.aosp.chakonati.services.Subscription
import dev.superboring.aosp.chakonati.services.Subscriptions
import dev.superboring.aosp.chakonati.x.logging.logDebug
import kotlinx.coroutines.FlowPreview
import java.nio.charset.StandardCharsets
import java.util.*

data class ChatSessionDetails(val remoteServer: String, val deviceId: Int)

object ChatSessionManager : SubscriptionListener {

    private val chatSessions: HashBasedTable<String, Int, ChatSession> = HashBasedTable.create()

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
        chatSession(ChatSessionDetails(
            messageNotification.from,
            messageNotification.deviceId,
        )).onNotification(messageNotification)
    }

    fun chatSession(details: ChatSessionDetails): ChatSession = synchronized(this) {
        return chatSessions[details.remoteServer, details.deviceId] ?: run {
            val chatSession = ChatSession(details.remoteServer)
            if (db.signalSessions().hasSession(details.remoteServer)) {
                val addressId = db.remoteAddresses().get(details.remoteServer).Id
                chatSession.useChat(db.chats().getByRemoteAddressId(addressId))
            }
            chatSessions.put(details.remoteServer, details.deviceId, chatSession)
            chatSession
        }
    }

    fun createFreshSession(remoteServer: String): ChatSession {
        return ChatSession(remoteServer)
    }

    fun restoreSessions() {
        logDebug("Restoring chat sessions")
        db.chats().allFull().forEach { chat ->
            logDebug("Restoring ${chat.id} – ${chat.remoteAddress.address}")
            startListening(chat)
        }

    }

    private fun startListening(chat: Chat) {
        startListeningOn(chatSession(chat.sessionDetails))
        logDebug("Listening to messages for ${chat.remoteAddressId} – ${chat.id}")
    }

    fun startListeningOn(chatSession: ChatSession) {
        chatSession.apply {
            listen {
                onMessage { msg, id ->
                    db.messages().add(
                        Message(
                            MessageFrom.THEM,
                            String(decrypt(msg), StandardCharsets.UTF_8)
                        ).asDBMessage(chat)
                    )
                    Messaging.confirmMessageReceived(id)
                }
            }
        }
    }

}