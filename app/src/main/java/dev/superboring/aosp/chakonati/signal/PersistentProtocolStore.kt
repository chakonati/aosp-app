package dev.superboring.aosp.chakonati.signal

import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.dao.insertWithIdentityKey
import dev.superboring.aosp.chakonati.persistence.dao.save
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.*
import kotlinx.coroutines.runBlocking
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.state.*
import org.whispersystems.libsignal.util.KeyHelper


fun generateIdentityKeyPair() : IdentityKeyPair {
    val identityKeyPairKeys: ECKeyPair = Curve.generateKeyPair()

    return IdentityKeyPair(
        IdentityKey(identityKeyPairKeys.publicKey),
        identityKeyPairKeys.privateKey
    )
}

private fun generateRegistrationId(): Int {
    return KeyHelper.generateRegistrationId(false)
}

class PersistentProtocolStore : SignalProtocolStore {

    val hasLocalRegistrationId
        get() = db.mySetup().get().registrationId != -1

    val hasIdentityKey
        get() = db.mySetup().count() > 0

    fun saveIdentityKeyPair(idKeyPair: IdentityKeyPair) {
        db.mySetup().get().apply {
            identityPrivateKey = idKeyPair.privateKey.serialize()
            identityPublicKey = idKeyPair.publicKey.serialize()
        }.save()
    }

    override fun getIdentityKeyPair() = db.mySetup().get().run {
        IdentityKeyPair(
            IdentityKey(identityPublicKey),
            Curve.decodePrivatePoint(identityPrivateKey)
        )
    }

    infix fun saveLocalRegistrationId(id: Int) =
        db.mySetup().get().apply { registrationId = id }.save()

    override fun getLocalRegistrationId() = db.mySetup().get().registrationId

    override fun saveIdentity(address: SignalProtocolAddress, identityKey: IdentityKey): Boolean {
        val existed = db.remoteAddresses().exists(address.deviceId, address.name)
        runBlocking {
            db.remoteAddresses() insertWithIdentityKey RemoteAddressAndIdentityKey(
                address = RemoteAddress from address,
                identityKey = RemoteIdentityKey(
                    publicKey = identityKey.serialize()
                )
            )
        }
        return existed
    }

    override fun isTrustedIdentity(
        address: SignalProtocolAddress,
        identityKey: IdentityKey,
        direction: IdentityKeyStore.Direction?
    ) = db.remoteAddresses().exists(address.deviceId, address.name)

    override fun getIdentity(address: SignalProtocolAddress) =
        db.remoteAddresses().get(address.deviceId, address.name).identityKey.signalIdentityKey

    override fun loadPreKey(preKeyId: Int) =
        db.localPreKeys().byPreKeyId(preKeyId).signalPreKeyRecord

    override fun storePreKey(preKeyId: Int, record: PreKeyRecord) {
        db.localPreKeys() insert LocalPreKey(
            preKeyId = preKeyId,
            prePublicKey = record.keyPair.publicKey.serialize(),
            prePrivateKey = record.keyPair.privateKey.serialize(),
        )
    }

    override fun containsPreKey(preKeyId: Int) =
        db.localPreKeys().hasKey(preKeyId)

    override fun removePreKey(preKeyId: Int) =
        db.localPreKeys() deleteByPreKeyId preKeyId

    override fun loadSession(address: SignalProtocolAddress?): SessionRecord {
        TODO("Not yet implemented")
    }

    override fun getSubDeviceSessions(name: String?): MutableList<Int> {
        TODO("Not yet implemented")
    }

    override fun storeSession(address: SignalProtocolAddress?, record: SessionRecord?) {
        TODO("Not yet implemented")
    }

    override fun containsSession(address: SignalProtocolAddress?): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteSession(address: SignalProtocolAddress?) {
        TODO("Not yet implemented")
    }

    override fun deleteAllSessions(name: String?) {
        TODO("Not yet implemented")
    }

    override fun loadSignedPreKey(signedPreKeyId: Int) =
        db.localSignedPreKeys().byPreKeyId(signedPreKeyId).signalSignedPreKeyRecord

    override fun loadSignedPreKeys(): MutableList<SignedPreKeyRecord> =
        db.localSignedPreKeys().all().map {
            SignedPreKeyRecord(it.signedPreKey)
        }.toMutableList()

    override fun storeSignedPreKey(signedPreKeyId: Int, record: SignedPreKeyRecord) {
        db.localSignedPreKeys() insert LocalSignedPreKey(
            preKeyId = signedPreKeyId,
            signedPreKey = record.serialize(),
        )
    }

    override fun containsSignedPreKey(signedPreKeyId: Int) =
        db.localSignedPreKeys() hasKey signedPreKeyId

    override fun removeSignedPreKey(signedPreKeyId: Int) =
        db.localSignedPreKeys() deleteByPreKeyId signedPreKeyId

}