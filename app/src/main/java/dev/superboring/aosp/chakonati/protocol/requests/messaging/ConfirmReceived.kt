package dev.superboring.aosp.chakonati.protocol.requests.messaging

import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response
import dev.superboring.aosp.chakonati.protocol.unpackError
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

class ConfirmReceivedRequest(
    private val messageId: Long,
    private val password: String
) : Request<ConfirmReceivedResponse>("Messaging.confirmReceived", 2) {
    override fun pack(packer: MessagePacker): Unit = packer.run {
        packLong(messageId)
        packString(password)
    }

    override fun newResponse() = ConfirmReceivedResponse()
}

class ConfirmReceivedResponse(
    var error: Error = null
) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        error = unpackError()
    }
}