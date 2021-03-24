package dev.superboring.aosp.chakonati.signal

import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.services.KeyExchange
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper
import java.security.SecureRandom

suspend fun generateAndPublishPreKeys() {
    if (!PersistentProtocolStore.hasIdentityKey) {
        PersistentProtocolStore.saveIdentityKeyPair(generateIdentityKeyPair())
    }
    if (!PersistentProtocolStore.hasLocalRegistrationId) {
        PersistentProtocolStore.saveLocalRegistrationId(
            KeyHelper.generateRegistrationId(true)
        )

        val preKeyPair: ECKeyPair = Curve.generateKeyPair()
        val signedPreKeyPair: ECKeyPair = Curve.generateKeyPair()
        val signedPreKeySignature = Curve.calculateSignature(
            PersistentProtocolStore.identityKeyPair.privateKey,
            signedPreKeyPair.publicKey.serialize()
        )

        val preKeyBundle = PreKeyBundle(
            PersistentProtocolStore.localRegistrationId,
            SecureRandom().nextInt(Short.MAX_VALUE.toInt()),
            SecureRandom().nextInt(Short.MAX_VALUE.toInt()), preKeyPair.publicKey,
            SecureRandom().nextInt(Short.MAX_VALUE.toInt()), signedPreKeyPair.publicKey,
            signedPreKeySignature,
            PersistentProtocolStore.identityKeyPair.publicKey
        )
        KeyExchange.publishPreKeyBundle(preKeyBundle, db.mySetup().get().relayServerPassword)

        PersistentProtocolStore.storePreKey(
            preKeyBundle.preKeyId,
            PreKeyRecord(preKeyBundle.preKeyId, preKeyPair)
        )
        PersistentProtocolStore.storeSignedPreKey(
            preKeyBundle.signedPreKeyId,
            SignedPreKeyRecord(
                preKeyBundle.signedPreKeyId,
                System.currentTimeMillis(), signedPreKeyPair, signedPreKeySignature
            )
        )
    }
}
