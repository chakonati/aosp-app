package dev.superboring.aosp.chakonati.protocol.requests

import dev.superboring.aosp.chakonati.protocol.Request
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

class EchoRequest(var value: String) : Request("Echo", 1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        value = unpackString()
    }

    override fun pack(packer: MessagePacker): Unit = packer.run {
        packString(value)
    }
}