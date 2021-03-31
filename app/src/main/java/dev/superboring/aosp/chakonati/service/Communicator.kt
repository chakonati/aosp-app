package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.protocol.*
import dev.superboring.aosp.chakonati.protocol.exceptions.UnsupportedMessageType
import dev.superboring.aosp.chakonati.protocol.exceptions.UntrackedResponsePacketException
import dev.superboring.aosp.chakonati.protocol.requests.basic.HelloRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class Communicator(private val server: String) : WebSocketServiceListener {

    private val openRequests = hashMapOf<RequestId, OpenRequest<*>>()
    private var hasSentHello = false

    private val serverUri
        get() = "ws://$server"

    private val webSocketService by lazy {
        WebSocketService(serverUri).apply {
            addListener(this@Communicator)
        }
    }

    suspend fun doHandshake() {
        val response = sendAsync(HelloRequest()).await()
        println("Received reply: ${response.reply}")
        hasSentHello = true
    }

    suspend infix fun <R : Response> send(request: Request<R>): R {
        if (!hasSentHello) {
            doHandshake()
        }
        return sendAsync(request).await()
    }

    private infix fun <R : Response> sendAsync(request: Request<R>): Deferred<R> {
        val deferred = CompletableDeferred<R>()
        @Suppress("UNCHECKED_CAST")
        openRequests[request.requestId] = OpenRequest(deferred as CompletableDeferred<Response>, request)
        webSocketService.send(request)
        return deferred
    }

    override fun onMessage(bytes: ByteArray) {
        val header = MessageHeader().apply { deserialize(bytes) }
        when (header.messageType) {
            MessageType.RESPONSE -> {
                val openRequest = openRequests[header.id] ?: throw UntrackedResponsePacketException(header)
                openRequest.request.newResponse().run {
                    deserialize(bytes)
                    openRequest.deferredResponse.complete(this)
                }
            }
            else -> throw UnsupportedMessageType(header.messageType)
        }
    }

    override fun onError(t: Throwable) {
        disconnect()
        openRequests.forEach { it.value.deferredResponse.completeExceptionally(t) }
    }

    fun disconnect() {
        webSocketService.disconnect()
    }

}