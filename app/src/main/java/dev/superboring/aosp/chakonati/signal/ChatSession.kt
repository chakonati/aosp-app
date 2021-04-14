package dev.superboring.aosp.chakonati.signal

import androidx.room.withTransaction
import dev.superboring.aosp.chakonati.domain.MessageNotification
import dev.superboring.aosp.chakonati.extras.msgpack.deserialize
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.Chat
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddress
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.service.OwnRelayServer
import dev.superboring.aosp.chakonati.service.SubscriptionListener
import dev.superboring.aosp.chakonati.services.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.whispersystems.libsignal.SessionBuilder
import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.state.SessionRecord
import kotlin.coroutines.CoroutineContext

typealias MessageListener = suspend ChatSession.(ByteArray) -> Unit

class ChatSession(
    val remoteServer: String
) : CoroutineScope, SubscriptionListener {

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

    init {
        OwnRelayServer.comm.apply {
            setSubscriptionListener(Subscriptions.MESSAGES, this@ChatSession)
        }
    }

    suspend fun startNew() {
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

        db.withTransaction {
            db.remoteAddresses() insert (RemoteAddress from signalAddress)
            db.chats() insert Chat(
                remoteAddressId = db.remoteAddresses().get(
                    deviceId,
                    remoteServer,
                ).address.Id,
                displayName = remoteServer
            )
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

    suspend fun listen(
        scope: SubscriptionScope.() -> Unit
    ) {
        subScope.scope()

        Subscription.subscribe(Subscriptions.MESSAGES)
    }

    override suspend fun onNotification(bytes: ByteArray) {
        subScope.messageListener?.let { messageListener ->
            val messageNotification = bytes.deserialize<MessageNotification>()
            val message = Messaging.getMessage(messageNotification.messageId)
            messageListener(this, message.encryptedMessage)
        }
    }

    fun disconnect() = communicator.disconnect()

    class SubscriptionScope(chatSession: ChatSession) {
        var messageListener: MessageListener? = null

        fun onMessage(listener: MessageListener) {
            messageListener = listener
        }
    }
}
