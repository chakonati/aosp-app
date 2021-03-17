package dev.superboring.aosp.chakonati.protocol.requests

import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker
import kotlin.reflect.KClass

const val argLen = 1

class EchoRequest(var value: String) : Request<EchoResponse>("Echo", argLen) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        value = unpackString()
    }

    override fun pack(packer: MessagePacker): Unit = packer.run {
        packString(value)
    }

    override fun newResponse() = EchoResponse()
}

class EchoResponse : Response(argLen)