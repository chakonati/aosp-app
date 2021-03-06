package dev.superboring.aosp.chakonati.protocol.requests.basic

import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response

data class EchoRequest(val value: String) : Request<EchoResponse>("echo")
data class EchoResponse(var echo: String) : Response()
