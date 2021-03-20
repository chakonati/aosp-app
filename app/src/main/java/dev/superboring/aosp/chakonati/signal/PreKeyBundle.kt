package dev.superboring.aosp.chakonati.signal

import androidx.compose.ui.input.key.Key
import dev.superboring.aosp.chakonati.services.KeyExchange
import kotlinx.coroutines.runBlocking
import org.whispersystems.libsignal.SessionBuilder
import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord

private val ALICE_ADDRESS = SignalProtocolAddress("alice", 1)
private val BOB_ADDRESS = SignalProtocolAddress("bob", 1)

const val originalMessage = "cool!"
val store = ProtocolStore()

suspend fun handlePreKeys() {
    val preKeyPair: ECKeyPair = Curve.generateKeyPair()
    val signedPreKeyPair: ECKeyPair = Curve.generateKeyPair()
    val signedPreKeySignature = Curve.calculateSignature(
        store.identityKeyPair.privateKey,
        signedPreKeyPair.publicKey.serialize()
    )

    val preKeyBundle = PreKeyBundle(
        store.localRegistrationId, 1,
        31337, preKeyPair.publicKey,
        22, signedPreKeyPair.publicKey,
        signedPreKeySignature,
        store.identityKeyPair.publicKey
    )
    KeyExchange.publishPreKeyBundle(preKeyBundle)

    store.storePreKey(31337, PreKeyRecord(preKeyBundle.preKeyId, preKeyPair))
    store.storeSignedPreKey(
        22,
        SignedPreKeyRecord(22, System.currentTimeMillis(), signedPreKeyPair, signedPreKeySignature)
    )
}

suspend fun sendMessage() {
    val sendingStore = ProtocolStore()
    val preKeyBundle = KeyExchange.preKeyBundle()

    val aliceSessionBuilder = SessionBuilder(sendingStore, BOB_ADDRESS)
    aliceSessionBuilder.process(preKeyBundle)

    val bobSessionCipher = SessionCipher(store, ALICE_ADDRESS)
    val aliceSessionCipher = SessionCipher(sendingStore, BOB_ADDRESS)

    val outgoingMessage = aliceSessionCipher.encrypt(originalMessage.toByteArray())

    val incomingMessage = PreKeySignalMessage(outgoingMessage.serialize())
    val plaintext = bobSessionCipher.decrypt(incomingMessage)

    println("Received message: ${String(plaintext)}")
}

suspend fun signalExample() {
    handlePreKeys()
    sendMessage()
}
