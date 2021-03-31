package dev.superboring.aosp.chakonati.protocol.requests.messaging

import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response
import dev.superboring.aosp.chakonati.protocol.packByteArray
import dev.superboring.aosp.chakonati.protocol.unpackError
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

class SendMessageRequest(
    private val encryptedMessage: ByteArray,
) : Request<SendMessageResponse>("Messaging.sendMessage", 1) {
    override fun pack(packer: MessagePacker): Unit = packer.run {
        packByteArray(encryptedMessage)
    }

    override fun newResponse() = SendMessageResponse()
}

class SendMessageResponse(
    var error: Error = null
) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        error = unpackError()
    }
}
