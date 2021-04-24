package dev.superboring.aosp.chakonati.signal

import com.google.common.collect.HashBasedTable
import dev.superboring.aosp.chakonati.domain.MessageNotification
import dev.superboring.aosp.chakonati.extras.msgpack.deserialize
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.service.OwnRelayServer
import dev.superboring.aosp.chakonati.service.SubscriptionListener
import dev.superboring.aosp.chakonati.services.Subscription
import dev.superboring.aosp.chakonati.services.Subscriptions
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

    suspend fun chatSession(remoteServer: String): ChatSession = synchronized(this) {
        // TODO: handle device IDs?
        val deviceId = 0
        return chatSessions[remoteServer, deviceId] ?: run {
            val chatSession = ChatSession(remoteServer)
            if (db.signalSessions().hasSession(remoteServer)) {
                val addressId = db.remoteAddresses().get(remoteServer).address.Id
                chatSession.useChat(db.chats().getByRemoteAddressId(addressId))
            }
            chatSessions.put(remoteServer, deviceId, chatSession)
            chatSession
        }
    }


}