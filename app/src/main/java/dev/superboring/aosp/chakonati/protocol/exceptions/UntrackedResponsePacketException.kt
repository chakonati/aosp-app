package dev.superboring.aosp.chakonati.protocol.exceptions

import dev.superboring.aosp.chakonati.protocol.ResponseHeader

class UntrackedResponsePacketException(responseHeader: ResponseHeader) : RuntimeException(
    "Invalid response header with request ID ${responseHeader.id}"
)