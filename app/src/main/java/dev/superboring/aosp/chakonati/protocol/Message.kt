package dev.superboring.aosp.chakonati.protocol

import kotlinx.serialization.Serializable

typealias Error = String?

enum class MessageType {
    REQUEST,
    RESPONSE,
    ONEWAY,
    STREAM,

    UNKNOWN
}

@Serializable
abstract class Message(
    open val id: RequestId,
    open val messageType: MessageType = MessageType.UNKNOWN,
    val data: ByteArray = byteArrayOf()
)

open class MessageHeader(
    override val id: RequestId,
    override val messageType: MessageType,
) : Message(id, messageType)