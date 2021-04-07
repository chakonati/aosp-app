package dev.superboring.aosp.chakonati.protocol.requests.messaging

import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response

data class SendMessageRequest(
    val encryptedMessage: ByteArray,
) : Request<SendMessageResponse>("Messaging.sendMessage")

data class SendMessageResponse(val error: Error) : Response()
