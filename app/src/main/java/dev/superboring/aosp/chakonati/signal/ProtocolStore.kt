package dev.superboring.aosp.chakonati.signal

import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECKeyPair
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
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

class ProtocolStore(
    idKeyPair: IdentityKeyPair = generateIdentityKeyPair(),
    regId: Int = generateRegistrationId()
) : InMemorySignalProtocolStore(idKeyPair, regId)