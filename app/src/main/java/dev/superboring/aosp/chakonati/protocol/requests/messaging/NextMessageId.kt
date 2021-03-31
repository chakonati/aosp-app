package dev.superboring.aosp.chakonati.protocol.requests.messaging

import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response
import dev.superboring.aosp.chakonati.protocol.unpackError
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

class NextMessageIdRequest(
    private val password: String
) : Request<NextMessageIdResponse>("Messaging.nextMessageId", 1) {
    override fun pack(packer: MessagePacker): Unit = packer.run {
        packString(password)
    }

    override fun newResponse() = NextMessageIdResponse()
}

class NextMessageIdResponse(
    var messageId: Long = 0,
    var hasNewMessages: Boolean = false,
    var error: Error = null
) : Response(3) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        messageId = unpackLong()
        hasNewMessages = unpackBoolean()
        error = unpackError()
    }
}