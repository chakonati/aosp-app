package dev.superboring.aosp.chakonati.signal

import dev.superboring.aosp.chakonati.persistence.dao.generateNewKeyId
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.services.KeyExchange
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper

object PreKeyBundle {
    suspend fun generatePreKeys(publish: Boolean = false) {
        PersistentProtocolStore.saveIdentityKeyPair(generateIdentityKeyPair())
        PersistentProtocolStore.saveLocalRegistrationId(
            KeyHelper.generateRegistrationId(true)
        )

        val preKeyPair = Curve.generateKeyPair()
        val signedPreKeyPair = Curve.generateKeyPair()
        val signedPreKeySignature = Curve.calculateSignature(
            PersistentProtocolStore.identityKeyPair.privateKey,
            signedPreKeyPair.publicKey.serialize()
        )

        val preKeyBundle = PreKeyBundle(
            PersistentProtocolStore.localRegistrationId,
            db.localPreKeys().generateNewKeyId(),
            db.localPreKeys().generateNewKeyId(), preKeyPair.publicKey,
            db.localPreKeys().generateNewKeyId(), signedPreKeyPair.publicKey,
            signedPreKeySignature,
            PersistentProtocolStore.identityKeyPair.publicKey
        )
        if (publish) {
            KeyExchange.publishPreKeyBundle(preKeyBundle)
        }

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
