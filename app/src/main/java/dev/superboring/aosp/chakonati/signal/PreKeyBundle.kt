package dev.superboring.aosp.chakonati.signal

import dev.superboring.aosp.chakonati.persistence.dao.generateNewKeyId
import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.services.KeyExchange
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper

object PreKeyBundle {
    suspend fun generateAndPublishPreKeys() {
        PersistentProtocolStore.saveIdentityKeyPair(generateIdentityKeyPair())
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
            db.localPreKeys().generateNewKeyId(),
            db.localPreKeys().generateNewKeyId(), preKeyPair.publicKey,
            db.localPreKeys().generateNewKeyId(), signedPreKeyPair.publicKey,
            signedPreKeySignature,
            PersistentProtocolStore.identityKeyPair.publicKey
        )
        KeyExchange.publishPreKeyBundle(preKeyBundle)

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