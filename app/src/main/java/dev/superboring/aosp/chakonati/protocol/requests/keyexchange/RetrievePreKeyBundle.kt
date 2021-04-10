package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.EmptyRequest
import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Response
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.ecc.ECPublicKey
import org.whispersystems.libsignal.state.PreKeyBundle

class RetrievePreKeyBundleRequest :
    EmptyRequest<RetrievePreKeyBundleResponse>("KeyExchange.preKeyBundle")

data class RetrievePreKeyBundleResponse(
    private val registrationId: Int,
    val deviceId: Int,
    private val preKeyId: Int?,
    private val preKey: ByteArray?,
    private val signedPreKeyId: Int,
    private val publicSignedPreKey: ByteArray,
    private val signedPreKeySignature: ByteArray,
    private val identityKey: ByteArray,
    val error: Error
) : Response() {

    fun preKeyBundle() =
        PreKeyBundle(
            registrationId,
            deviceId,
            preKeyId ?: 0,
            preKey?.let { ECPublicKey(preKey) },
            signedPreKeyId,
            ECPublicKey(publicSignedPreKey),
            signedPreKeySignature,
            IdentityKey(identityKey)
        )
}