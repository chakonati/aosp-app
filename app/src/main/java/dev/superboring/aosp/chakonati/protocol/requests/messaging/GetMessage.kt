package dev.superboring.aosp.chakonati.protocol.requests.messaging

import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response

data class EncryptedMessage(
    val id: Long,
    val encryptedMessage: ByteArray,
)

data class GetMessageRequest(
    val messageId: Long,
    val password: String
) : Request<ConfirmReceivedResponse>("Messaging.confirmReceived")

data class GetMessageResponse(
    val message: EncryptedMessage?,
    val error: Error
) : Response()