package dev.superboring.aosp.chakonati.protocol

import org.msgpack.core.MessageUnpacker

abstract class ErrorResponse(var error: Error = null) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        error = unpackError()
    }
}