package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.superboring.aosp.chakonati.protocol.ErrorResponse
import dev.superboring.aosp.chakonati.protocol.Request
import org.whispersystems.libsignal.state.PreKeyBundle

data class PreKeyBundlePublishRequest(
    @JsonIgnore val preKeyBundle: PreKeyBundle,
    val password: String,
) : Request<PreKeyBundlePublishResponse>("KeyExchange.publishPreKeyBundle") {
    val registrationId = preKeyBundle.registrationId
    val deviceId = preKeyBundle.deviceId
    val preKeyId = preKeyBundle.preKeyId
    val publicPreKey = preKeyBundle.preKey.serialize()
    val signedPreKeyId = preKeyBundle.signedPreKeyId
    val publicSignedPreKey = preKeyBundle.signedPreKey.serialize()
    val signedPreKeySignature = preKeyBundle.signedPreKeySignature
    val identityKey = preKeyBundle.identityKey.publicKey.serialize()
}

class PreKeyBundlePublishResponse : ErrorResponse()
