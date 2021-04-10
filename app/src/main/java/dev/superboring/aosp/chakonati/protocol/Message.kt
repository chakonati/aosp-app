package dev.superboring.aosp.chakonati.protocol

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

typealias Error = String?
typealias MessageType = Int

object MessageTypes {
    const val REQUEST = 0
    const val RESPONSE = 1
    const val ONEWAY = 2
    const val STREAM = 3
    const val NOTIFICATION = 4

    const val UNKNOWN = -1
}

@Serializable
abstract class Message(
    open val id: RequestId,
    open val messageType: MessageType = MessageTypes.UNKNOWN,
)

@JsonIgnoreProperties(ignoreUnknown = true)
open class MessageHeader(
    override val id: RequestId,
    override val messageType: MessageType,
) : Message(id, messageType)
