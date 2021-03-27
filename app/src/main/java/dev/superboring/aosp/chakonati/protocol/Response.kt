package dev.superboring.aosp.chakonati.protocol

import org.msgpack.core.MessagePacker

abstract class Response(
    argLen: Int = 0,
    id: RequestId = 0L,
) : Message<Response>(argLen, id, MessageType.RESPONSE) {
    override fun pack(packer: MessagePacker) {

    }
}
