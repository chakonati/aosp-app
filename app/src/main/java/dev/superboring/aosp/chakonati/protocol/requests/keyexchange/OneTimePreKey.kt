package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.*
import org.msgpack.core.MessageUnpacker

class OneTimePreKeyRequest : EmptyRequest<OneTimePreKeyResponse>("KeyExchange.oneTimePreKey") {
    override fun newResponse() = OneTimePreKeyResponse()
}

class OneTimePreKeyResponse(var key: OneTimePreKey? = null, var error: Error = null) :
    Response(2) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        unpackOptionally { key = OneTimePreKey().apply { unpack(this) } }
        error = unpackError()
    }
}