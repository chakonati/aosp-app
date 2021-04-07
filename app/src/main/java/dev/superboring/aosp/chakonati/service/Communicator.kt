package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.extras.msgpack.deserialize
import dev.superboring.aosp.chakonati.protocol.*
import dev.superboring.aosp.chakonati.protocol.exceptions.UnsupportedMessageType
import dev.superboring.aosp.chakonati.protocol.exceptions.UntrackedResponsePacketException
import dev.superboring.aosp.chakonati.protocol.requests.basic.HelloRequest
import dev.superboring.aosp.chakonati.protocol.requests.basic.HelloResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlin.reflect.KClass

class Communicator(private val server: String) : WebSocketServiceListener {

    private val openRequests = hashMapOf<RequestId, OpenRequest<*>>()
    var hasSentHello = false
        private set

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

    suspend inline infix fun <reified R : Response> send(request: Request<R>): R {
        if (!hasSentHello) {
            doHandshake()
        }
        return sendAsync(request).await()
    }

    fun <R : Response> sendAsync(request: Request<R>): Deferred<R> {
        val deferred = CompletableDeferred<R>()
        synchronized(openRequests) {
            @Suppress("UNCHECKED_CAST")
            openRequests[request.id] =
                OpenRequest(deferred as CompletableDeferred<Response>, request)
        }
        webSocketService.send(request)
        return deferred
    }

    override fun onMessage(bytes: ByteArray) {
        val message = bytes.deserialize<Message>()
        when (message.messageType) {
            MessageType.RESPONSE -> {
                synchronized(openRequests) {
                    val openRequest =
                        openRequests[message.id] ?: throw UntrackedResponsePacketException(message)
                    openRequest.run {
                        deferredResponse.complete(message.data.deserialize())
                    }
                    openRequests -= message.id
                }
            }
            else -> throw UnsupportedMessageType(message.messageType)
        }
    }

    override fun onError(t: Throwable) {
        disconnect()
        synchronized(openRequests) {
            openRequests.forEach { it.value.deferredResponse.completeExceptionally(t) }
        }
    }

    fun disconnect() {
        webSocketService.disconnect()
    }

}