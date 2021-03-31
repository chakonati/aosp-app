package dev.superboring.aosp.chakonati.protocol.requests.messaging

import dev.superboring.aosp.chakonati.protocol.*
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

data class EncryptedMessage(
    var id: Long = 0,
    var encryptedMessage: ByteArray = byteArrayOf(),
) : Packable<EncryptedMessage> {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        id = unpackLong()
        encryptedMessage = unpackByteArray()
    }

    override fun pack(packer: MessagePacker) {
        TODO("Not needed")
    }
}

class GetMessageRequest(
    private val messageId: Long,
    private val password: String
) : Request<ConfirmReceivedResponse>("Messaging.confirmReceived", 2) {
    override fun pack(packer: MessagePacker): Unit = packer.run {
        packLong(messageId)
        packString(password)
    }

    override fun newResponse() = ConfirmReceivedResponse()
}

class GetMessageResponse(
    var message: EncryptedMessage? = null,
    var error: Error = null
) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        message = unpackOptional { EncryptedMessage().apply { unpack(unpacker) } }
        error = unpackError()
    }
}