package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.persistence.entities.PrePublicKey
import dev.superboring.aosp.chakonati.protocol.*
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

data class OneTimePreKey(
    var preKeyId: Int = 0,
    var preKey: PrePublicKey = byteArrayOf(),
) : Packable<OneTimePreKey> {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        preKeyId = unpackInt()
        preKey = unpackByteArray()
    }

    override fun pack(packer: MessagePacker): Unit = packer.run {
        packInt(preKeyId)
        packByteArray(preKey)
    }
}

class OneTimePreKeysPublishRequest(
    private val preKeys: List<OneTimePreKey>,
    private val password: String
) :
    Request<OneTimePreKeysPublishResponse>("KeyExchange.publishOneTimePreKeys", 2) {
    override fun pack(packer: MessagePacker): Unit = packer.run {
        pack(preKeys)
        packString(password)
    }

    override fun newResponse() = OneTimePreKeysPublishResponse()

}

class OneTimePreKeysPublishResponse(var error: Error = null) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        error = unpackError()
    }
}