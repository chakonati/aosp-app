package dev.superboring.aosp.chakonati.protocol

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

typealias RequestId = Long

private var nextId: RequestId = 0L

abstract class Request<R : Response>(
    var action: String,
) : Message(nextId++, MessageTypes.REQUEST)
abstract class EmptyRequest<R : Response>(action: String) : Request<R>(action)

@JsonIgnoreProperties(ignoreUnknown = true)
class EmptyRequestOnly(action: String) : Request<EmptyResponse>(action)