package dev.superboring.aosp.chakonati.protocol.requests

import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response
import dev.superboring.aosp.chakonati.protocol.packByteArray
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.state.PreKeyBundle

class PreKeyBundlePublishRequest(var preKeyBundle: PreKeyBundle) :
    Request<PreKeyBundlePublishResponse>("KeyExchange.publishPreKeyBundle", 8) {

    override fun newResponse(): PreKeyBundlePublishResponse {
        return PreKeyBundlePublishResponse()
    }

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
        }
    }

}

class PreKeyBundlePublishResponse(var error: String? = null) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        if (!tryUnpackNil()) {
            error = unpackString()
        }
    }
}