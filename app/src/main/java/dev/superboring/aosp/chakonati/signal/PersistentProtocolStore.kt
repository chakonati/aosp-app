package dev.superboring.aosp.chakonati.signal

import dev.superboring.aosp.chakonati.persistence.dao.*
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.*
import dev.superboring.aosp.chakonati.x.logging.logDebug
import kotlinx.coroutines.runBlocking
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.state.*

fun generateIdentityKeyPair(): IdentityKeyPair {
    val identityKeyPairKeys: ECKeyPair = Curve.generateKeyPair()

    return IdentityKeyPair(
        IdentityKey(identityKeyPairKeys.publicKey),
        identityKeyPairKeys.privateKey
    )
}

object PersistentProtocolStore : SignalProtocolStore {

    val hasLocalRegistrationId
        get() = localRegistrationId != -1

    val hasIdentityKey
        get() = db.mySetup().get().identityPrivateKey.isNotEmpty()

    fun saveIdentityKeyPair(idKeyPair: IdentityKeyPair) {
        logDebug("Saving identity key pair $idKeyPair")
        runBlocking {
            db.mySetup().get().apply {
                identityPrivateKey = idKeyPair.privateKey.serialize()
                identityPublicKey = idKeyPair.publicKey.serialize()
            }.save()
        }
    }

    override fun getIdentityKeyPair() = db.mySetup().get().run {
        logDebug("Getting identity key pair")
        IdentityKeyPair(
            IdentityKey(identityPublicKey),
            Curve.decodePrivatePoint(identityPrivateKey)
        )
    }

    infix fun saveLocalRegistrationId(id: Int) = runBlocking {
        logDebug("Saving local registration ID $id")
        db.mySetup().get().apply { registrationId = id }.save()
    }

    override fun getLocalRegistrationId() = db.mySetup().get().registrationId.apply {
        this@PersistentProtocolStore.logDebug("Got local registration ID $this")
    }

    override fun saveIdentity(address: SignalProtocolAddress, identityKey: IdentityKey): Boolean {
        logDebug("Saving identity $identityKey for $address")
        val existed = db.remoteAddresses().exists(address.name)
        runBlocking {
            db.remoteAddresses().insertWithIdentityKey(
                RemoteAddress from address,
                RemoteIdentityKey(
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
    ): Boolean {
        val storedFingerprint = getIdentity(address).fingerprint
        logDebug("Identity trust check (${identityKey.fingerprint} vs ${storedFingerprint})")
        return db.remoteAddresses().exists(address.name) &&
                storedFingerprint.equals(identityKey.fingerprint).apply {
                    this@PersistentProtocolStore.logDebug(
                        "is $address with $identityKey in direction $direction trusted? – $this"
                    )
                }
    }

    override fun getIdentity(address: SignalProtocolAddress): IdentityKey {
        logDebug("Getting identity for ${address.name}")
        return db.remoteAddresses().get(address.name).identityKey?.signalIdentityKey?.apply {
            this@PersistentProtocolStore.logDebug("Got identity for $address: $this")
        } ?: throw RuntimeException("identity key is null")
    }

    fun loadLastPreKey() = db.localPreKeys().lastKey().apply {
        this@PersistentProtocolStore.logDebug("Loaded last pre key")
    }

    override fun loadPreKey(preKeyId: Int) =
        db.localPreKeys().byPreKeyId(preKeyId).signalPreKeyRecord.apply {
            this@PersistentProtocolStore.logDebug("Loaded pre-key for ID $preKeyId")
        }

    override fun storePreKey(preKeyId: Int, record: PreKeyRecord) {
        logDebug("Storing pre-key record (ID $preKeyId)")
        db.localPreKeys() insert LocalPreKey(
            preKeyId = preKeyId,
            prePublicKey = record.keyPair.publicKey.serialize(),
            prePrivateKey = record.keyPair.privateKey.serialize(),
        )
    }

    override fun containsPreKey(preKeyId: Int) =
        db.localPreKeys().hasKey(preKeyId).apply {
            this@PersistentProtocolStore.logDebug("Contains pre-key $preKeyId? – $this")
        }

    override fun removePreKey(preKeyId: Int) =
        db.localPreKeys() deleteByPreKeyId preKeyId

    override fun loadSession(address: SignalProtocolAddress): SessionRecord {
        logDebug("Loading session for $address")
        return db.signalSessions().run {
            if (hasSession(address.name)) {
                logDebug("Already have a session for $address")
                get(address.name).signalSession
            } else {
                logDebug("New session record for $address")
                SessionRecord()
            }
        }
    }

    override fun getSubDeviceSessions(name: String) =
        db.signalSessions().getDeviceIds(name).toMutableList()

    override fun storeSession(address: SignalProtocolAddress, record: SessionRecord) =
        db.signalSessions().upsertWithAddress(
            SignalSession from record,
            db.remoteAddresses().get(address.name),
        )

    override fun containsSession(address: SignalProtocolAddress) =
        db.signalSessions().hasSession(RemoteAddress from address)

    override fun deleteSession(address: SignalProtocolAddress) =
        db.signalSessions() deleteByRemoteAddress (RemoteAddress from address)

    override fun deleteAllSessions(name: String) = runBlocking {
        db.signalSessions() deleteByAddressName name
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


