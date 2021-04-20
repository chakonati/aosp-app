package dev.superboring.aosp.chakonati.signal

import com.google.common.collect.HashBasedTable
import dev.superboring.aosp.chakonati.domain.MessageNotification
import dev.superboring.aosp.chakonati.extras.msgpack.deserialize
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

    fun chatSession(remoteServer: String): ChatSession {
        // TODO: handle device IDs?
        val deviceId = 0
        return chatSessions[remoteServer, deviceId] ?: run {
            val chatSession = ChatSession(remoteServer)
            chatSessions.put(remoteServer, deviceId, chatSession)
            chatSession
        }
    }


}