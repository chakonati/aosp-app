package dev.superboring.aosp.chakonati.protocol.requests.messaging

import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response

data class NextMessageIdRequest(
    val password: String
) : Request<NextMessageIdResponse>("Messaging.nextMessageId")

data class NextMessageIdResponse(
    val messageId: Long,
    val hasNewMessages: Boolean,
    val error: Error
) : Response()
