package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.requests.HelloRequest

class Communicator(private val server: String) {

    private val serverUri
        get() = "ws://$server"

    private val webSocketService by lazy {
        WebSocketService(serverUri).apply {
            send(HelloRequest())
        }
    }

    infix fun send(request: Request) {
        webSocketService.send(request)
    }

}