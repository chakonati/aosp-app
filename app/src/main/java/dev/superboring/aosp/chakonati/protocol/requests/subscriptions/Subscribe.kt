package dev.superboring.aosp.chakonati.protocol.requests.subscriptions

import dev.superboring.aosp.chakonati.protocol.ErrorResponse
import dev.superboring.aosp.chakonati.protocol.Request

data class SubscribeRequest(
    val subName: String,
) : Request<SubscribeResponse>("subscribe")

class SubscribeResponse : ErrorResponse()