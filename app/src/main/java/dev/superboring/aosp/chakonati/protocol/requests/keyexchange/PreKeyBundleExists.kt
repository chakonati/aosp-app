package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.EmptyRequest
import dev.superboring.aosp.chakonati.protocol.Response
import org.msgpack.core.MessageUnpacker

class PreKeyBundleExistsRequest :
    EmptyRequest<PreKeyBundleExistsResponse>("KeyExchange.preKeyBundleExists") {
    override fun newResponse() = PreKeyBundleExistsResponse()
}

class PreKeyBundleExistsResponse(var exists: Boolean = false) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        exists = unpackBoolean()
    }
}