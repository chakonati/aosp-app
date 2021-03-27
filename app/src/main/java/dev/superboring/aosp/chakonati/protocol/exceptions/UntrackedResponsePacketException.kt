package dev.superboring.aosp.chakonati.protocol.exceptions

import dev.superboring.aosp.chakonati.protocol.MessageHeader
import dev.superboring.aosp.chakonati.protocol.MessageType

class UntrackedResponsePacketException(responseHeader: MessageHeader) : RuntimeException(
    "Invalid response header with request ID ${responseHeader.id}"
)

class UnsupportedMessageType(type: MessageType) : RuntimeException(
    "Unsupported message type: $type"
)