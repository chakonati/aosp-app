package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.ErrorResponse
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.packByteArray
import org.msgpack.core.MessagePacker
import org.whispersystems.libsignal.state.PreKeyBundle

class PreKeyBundlePublishRequest(
    var preKeyBundle: PreKeyBundle,
    var password: String,
) :
    Request<PreKeyBundlePublishResponse>("KeyExchange.publishPreKeyBundle", 9) {

    override fun newResponse() = PreKeyBundlePublishResponse()

    override fun pack(packer: MessagePacker): Unit = packer.run {
        preKeyBundle.run {
            packInt(registrationId)
            packInt(deviceId)
            packInt(preKeyId)
            packByteArray(preKey.publicKeyBytes)
            packInt(signedPreKeyId)
            packByteArray(signedPreKey.publicKeyBytes)
            packByteArray(signedPreKeySignature)
            packByteArray(identityKey.serialize())
            packString(password)
        }
    }

}

class PreKeyBundlePublishResponse : ErrorResponse()