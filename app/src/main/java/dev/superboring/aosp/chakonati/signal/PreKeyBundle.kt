package dev.superboring.aosp.chakonati.signal

import dev.superboring.aosp.chakonati.services.KeyExchange
import org.whispersystems.libsignal.SessionBuilder
import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper
import java.security.SecureRandom

private val ALICE_ADDRESS = SignalProtocolAddress("chat.alice.tld", 1)
private val BOB_ADDRESS = SignalProtocolAddress("chat.bob.tld", 1)

const val originalMessage = "cool!"
val store = PersistentProtocolStore()

suspend fun handlePreKeys() {
    if (!store.hasIdentityKey) {
        store.saveIdentityKeyPair(generateIdentityKeyPair())
    }
    if (!store.hasLocalRegistrationId) {
        store.saveLocalRegistrationId(KeyHelper.generateRegistrationId(true))
    }

    val preKeyPair: ECKeyPair = Curve.generateKeyPair()
    val signedPreKeyPair: ECKeyPair = Curve.generateKeyPair()
    val signedPreKeySignature = Curve.calculateSignature(
        store.identityKeyPair.privateKey,
        signedPreKeyPair.publicKey.serialize()
    )

    val preKeyBundle = PreKeyBundle(
        store.localRegistrationId, SecureRandom().nextInt(Short.MAX_VALUE.toInt()),
        SecureRandom().nextInt(Short.MAX_VALUE.toInt()), preKeyPair.publicKey,
        SecureRandom().nextInt(Short.MAX_VALUE.toInt()), signedPreKeyPair.publicKey,
        signedPreKeySignature,
        store.identityKeyPair.publicKey
    )
    KeyExchange.publishPreKeyBundle(preKeyBundle)

    store.storePreKey(preKeyBundle.preKeyId, PreKeyRecord(preKeyBundle.preKeyId, preKeyPair))
    store.storeSignedPreKey(
        preKeyBundle.signedPreKeyId,
        SignedPreKeyRecord(
            preKeyBundle.signedPreKeyId,
            System.currentTimeMillis(), signedPreKeyPair, signedPreKeySignature
        )
    )
}

suspend fun sendMessage() {
    val sendingStore = PersistentProtocolStore()
    val preKeyBundle = KeyExchange.preKeyBundle()

    sendingStore.saveIdentity(BOB_ADDRESS, preKeyBundle.identityKey)
    store.saveIdentity(ALICE_ADDRESS, preKeyBundle.identityKey)

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
