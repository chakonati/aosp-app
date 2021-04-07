package dev.superboring.aosp.chakonati.protocol.requests.messaging

import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response

data class ConfirmReceivedRequest(
    val messageId: Long,
    val password: String
) : Request<ConfirmReceivedResponse>("Messaging.confirmReceived")

data class ConfirmReceivedResponse(var error: Error) : Response()