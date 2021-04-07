package dev.superboring.aosp.chakonati.protocol.exceptions

import dev.superboring.aosp.chakonati.protocol.Message
import dev.superboring.aosp.chakonati.protocol.MessageHeader
import dev.superboring.aosp.chakonati.protocol.MessageType

class UntrackedResponsePacketException(message: Message) : RuntimeException(
    "Invalid response header with request ID ${message.id}"
)

class UnsupportedMessageType(type: MessageType) : RuntimeException(
    "Unsupported message type: $type"
)