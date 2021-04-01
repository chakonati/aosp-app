package dev.superboring.aosp.chakonati.signal

import androidx.room.withTransaction
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.Chat
import dev.superboring.aosp.chakonati.persistence.entities.RemoteAddress
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.services.RemoteKeyExchange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.state.SessionRecord
import org.whispersystems.libsignal.state.SessionState
import kotlin.coroutines.CoroutineContext

class ChatSession(
    val remoteServer: String
) : CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val communicator = Communicator(remoteServer)
    private val keyExchange by lazy { RemoteKeyExchange(communicator) }
    private lateinit var signalAddress: SignalProtocolAddress

    private var signalSessionFromStore
        get() = PersistentProtocolStore.loadSession(signalAddress)
        set(value) = PersistentProtocolStore.storeSession(signalAddress, value)

    private lateinit var signalSessionInternal: SessionRecord
    private val signalSessionLazy by lazy { signalSessionFromStore }

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

    suspend fun startNew() {
        communicator.doHandshake()
        if (!keyExchange.preKeyBundleExists()) {
            throw RuntimeException("Pre-key bundle does not exist on remote server")
        }
        val deviceId = keyExchange.deviceId()
        val preKeyBundle = keyExchange.preKeyBundle()
        signalAddress = SignalProtocolAddress(remoteServer, deviceId)
        if (!signalSession.isFresh) {
            throw RuntimeException("New session is not fresh")
        }

        signalSession.setState(
            SessionState.initializeAliceSession(
                PersistentProtocolStore.identityKeyPair,
                PersistentProtocolStore.loadLastPreKey().signalPreKeyRecord.keyPair,
                preKeyBundle.identityKey,
                preKeyBundle.signedPreKey,
                preKeyBundle.preKey,
            )
        )

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

    fun disconnect() = communicator.disconnect()
}