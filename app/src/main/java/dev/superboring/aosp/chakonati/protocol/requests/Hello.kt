package dev.superboring.aosp.chakonati.protocol.requests

import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker
import kotlin.reflect.KClass

class HelloRequest : Request<HelloResponse>("hello") {
    override fun newResponse() = HelloResponse()
}

class HelloResponse(var reply: String = "") : Response() {
    override fun pack(packer: MessagePacker): Unit = packer.run {
        packString(reply)
    }

    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        reply = unpackString()
    }
}