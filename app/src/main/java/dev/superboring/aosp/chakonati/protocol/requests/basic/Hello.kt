package dev.superboring.aosp.chakonati.protocol.requests.basic

import dev.superboring.aosp.chakonati.protocol.EmptyRequest
import dev.superboring.aosp.chakonati.protocol.Response

class HelloRequest : EmptyRequest<HelloResponse>("hello")

data class HelloResponse(var reply: String) : Response()
