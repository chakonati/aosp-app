package dev.superboring.aosp.chakonati.signal

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
val receivingStore = ProtocolStore()
val sendingStore = ProtocolStore()

fun handlePreKeys() {
    val receivingPreKey: ECKeyPair = Curve.generateKeyPair()
    val receivingSignedPreKeyPair: ECKeyPair = Curve.generateKeyPair()
    val receivingSignedPreKeySignature = Curve.calculateSignature(
        receivingStore.identityKeyPair.privateKey,
        receivingSignedPreKeyPair.publicKey.serialize()
    )

    val receivingPreKeyBundle = PreKeyBundle(
        receivingStore.localRegistrationId, 1,
        31337, receivingPreKey.publicKey,
        22, receivingSignedPreKeyPair.publicKey,
        receivingSignedPreKeySignature,
        receivingStore.identityKeyPair.publicKey
    )
    receivingStore.storePreKey(31337, PreKeyRecord(receivingPreKeyBundle.preKeyId, receivingPreKey))
    receivingStore.storeSignedPreKey(
        22,
        SignedPreKeyRecord(22, System.currentTimeMillis(), receivingSignedPreKeyPair, receivingSignedPreKeySignature)
    )

    SessionBuilder(sendingStore, BOB_ADDRESS).apply {
        process(receivingPreKeyBundle)
    }
}

fun sendMessage() {
    val bobSessionCipher = SessionCipher(receivingStore, ALICE_ADDRESS)
    val aliceSessionCipher = SessionCipher(sendingStore, BOB_ADDRESS)

    val outgoingMessage = aliceSessionCipher.encrypt(originalMessage.toByteArray())

    val incomingMessage = PreKeySignalMessage(outgoingMessage.serialize())
    val plaintext = bobSessionCipher.decrypt(incomingMessage)

    println("Received message: ${String(plaintext)}")
}

fun signalExample() {
    handlePreKeys()
    sendMessage()
}
