package dev.superboring.aosp.chakonati.signal

import androidx.room.withTransaction
import dev.superboring.aosp.chakonati.domain.MessageNotification
import dev.superboring.aosp.chakonati.extensions.kotlin.notThen
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.Chat
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddress
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.services.Messaging
import dev.superboring.aosp.chakonati.services.RemoteKeyExchange
import dev.superboring.aosp.chakonati.services.RemoteMessaging
import dev.superboring.aosp.chakonati.x.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.state.SessionRecord
import kotlin.coroutines.CoroutineContext

typealias MessageListener = suspend ChatSession.(ByteArray) -> Unit

class ChatSession(
    val remoteServer: String
) : CoroutineScope {

    lateinit var chat: Chat

    private val job by lazy { Job() }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val communicator by lazy { Communicator(remoteServer) }
    private val keyExchange by lazy { RemoteKeyExchange(communicator) }
    private lateinit var signalAddress: SignalProtocolAddress

    private var signalSessionFromStore
        get() = PersistentProtocolStore.loadSession(signalAddress)
        set(value) = PersistentProtocolStore.storeSession(signalAddress, value)

    private lateinit var signalSessionInternal: SessionRecord
    private val signalSessionLazy by lazy { signalSessionFromStore }

    val subScope by lazy { SubscriptionScope(this) }
    private val remoteMessaging by lazy { RemoteMessaging(communicator) }

    private var signalSession: SessionRecord
        get() {
            if (!::signalSessionInternal.isInitialized) {
                signalSessionInternal = signalSessionLazy
            }
            return signalSessionInternal
        }
        set(value) {
            signalSessionFromStore = value
            signalSessionInternal = signalSessionFromStore
        }

    fun useChat(chat: Chat) {
        this.chat = chat
    }

    suspend fun startNew(): Chat {
        communicator.doHandshake()
        if (!keyExchange.preKeyBundleExists()) {
            throw RuntimeException("Pre-key bundle does not exist on remote server")
        }
        val deviceId = keyExchange.deviceId()
        //val preKeyBundle = keyExchange.preKeyBundle()
        signalAddress = SignalProtocolAddress(remoteServer, deviceId)
        /*if (!signalSession.isFresh) {
            throw RuntimeException("New session is not fresh")
        }

        PersistentProtocolStore.saveIdentity(signalAddress, preKeyBundle.identityKey)
        SessionBuilder(PersistentProtocolStore, signalAddress).apply {
            //process(preKeyBundle)
        }*/

        return db.withTransaction {
            db.remoteAddresses() insert (RemoteAddress from signalAddress)
            chat = Chat(
                remoteAddressId = db.remoteAddresses().get(
                    remoteServer,
                ).address.Id,
                displayName = remoteServer
            )
            db.chats() insert chat
            chat
        }
    }

    suspend fun <R> useExisting(fn: suspend ChatSession.() -> R): R {
        return fn()
        val deviceId = PersistentProtocolStore.getSubDeviceSessions(remoteServer)[0]
        signalAddress = SignalProtocolAddress(remoteServer, deviceId)
        return fn()
    }

    fun encrypt(message: ByteArray): ByteArray {
        return message
        // TODO: make this work
        SessionCipher(PersistentProtocolStore, signalAddress).run {
            return encrypt(message).serialize()
        }
    }

    fun decrypt(message: ByteArray): ByteArray {
        return message
        // TODO: make this work
        SessionCipher(PersistentProtocolStore, signalAddress).run {
            return decrypt(PreKeySignalMessage(message))
        }
    }

    suspend fun onNotification(notification: MessageNotification) {
        try {
            ::chat.isInitialized.notThen {
                startNew()
            }
            subScope.messageListener?.let { messageListener ->
                val message = Messaging.getMessage(notification.messageId)
                messageListener(this, message.encryptedMessage)
            }
        } catch (e: Exception) {
            debug {
                println("oops, notification delivery FAILED")
                println(e)
            }
        }
    }

    fun listen(
        scope: SubscriptionScope.() -> Unit
    ) {
        subScope.scope()
    }

    fun disconnect() = communicator.disconnect()

    class SubscriptionScope(chatSession: ChatSession) {
        var messageListener: MessageListener? = null

        fun onMessage(listener: MessageListener) {
            messageListener = listener
        }
    }
}
