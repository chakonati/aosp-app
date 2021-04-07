package dev.superboring.aosp.chakonati.protocol

abstract class Response(override val id: RequestId = 0L) :
    Message(id, MessageType.RESPONSE)
