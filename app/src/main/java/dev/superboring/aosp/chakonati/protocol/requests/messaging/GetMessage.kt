package dev.superboring.aosp.chakonati.protocol.requests.messaging

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.superboring.aosp.chakonati.exceptions.UnexpectedNull
import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response

data class EncryptedMessage(
    val messageId: Long,
    val encryptedMessage: ByteArray,
)

data class GetMessageRequest(
    val messageId: Long,
    val password: String
) : Request<GetMessageResponse>("Messaging.getMessage")

@JsonIgnoreProperties(ignoreUnknown = true)
data class GetMessageResponse(
    val messageId: Long,
    val encryptedMessage: ByteArray?,
    val error: Error
) : Response() {

    val message get() = EncryptedMessage(
        messageId,
        encryptedMessage ?: throw UnexpectedNull(
            "${::encryptedMessage.name} cannot be null if there is no error. " +
                    "You should check if there is an error first."
        ),
    )

}

