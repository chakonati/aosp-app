package dev.superboring.aosp.chakonati.protocol

typealias RequestId = Long

private var nextId: RequestId = 0L

abstract class Request<R : Response>(
    var action: String,
) : Message(nextId++, MessageType.REQUEST) {
    /**
     * Create an empty response, possibly with defaults
     */
    abstract fun createResponse(): R
}

abstract class EmptyRequest<R : Response>(action: String) : Request<R>(action)