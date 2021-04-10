package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.extras.msgpack.deserialize
import dev.superboring.aosp.chakonati.extras.msgpack.serialized
import dev.superboring.aosp.chakonati.protocol.*
import dev.superboring.aosp.chakonati.protocol.exceptions.UnsupportedMessageType
import dev.superboring.aosp.chakonati.protocol.exceptions.UntrackedResponsePacketException
import dev.superboring.aosp.chakonati.protocol.requests.basic.HelloRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class Communicator(private val server: String) : WebSocketServiceListener {

    private val openRequests = hashMapOf<RequestId, OpenRequest>()
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
        val response = sendWithoutChecks(HelloRequest())
        println("Received reply: ${response.reply}")
        hasSentHello = true
    }

    suspend inline infix fun <reified R : Response> send(request: Request<R>): R {
        if (!hasSentHello) {
            doHandshake()
        }
        return sendWithoutChecks(request)
    }

    suspend inline infix fun <reified R : Response> sendWithoutChecks(request: Request<R>): R {
        return sendAsync(request).await().deserialize()
    }

    fun <R : Response> sendAsync(request: Request<R>): Deferred<ByteArray> {
        val deferred = CompletableDeferred<ByteArray>()
        synchronized(openRequests) {
            openRequests[request.id] = OpenRequest(deferred, request)
        }
        val requestHeader = request
            .serialized.deserialize<EmptyRequestOnly>()
            .serialized.deserialize<Map<String, Any?>>()
        val dataMap = request.serialized.deserialize<MutableMap<String, Any>>()
        requestHeader.keys.forEach { dataMap -= it }
        val requestMap = requestHeader.toMutableMap()
        requestMap["data"] = if (dataMap.isNotEmpty()) {
            dataMap
        } else {
            null
        }
        webSocketService.send(requestMap)
        return deferred
    }

    override fun onMessage(bytes: ByteArray) {
        val message = bytes.deserialize<MessageHeader>()
        when (message.messageType) {
            MessageTypes.RESPONSE -> {
                synchronized(openRequests) {
                    val openRequest =
                        openRequests[message.id] ?: throw UntrackedResponsePacketException(message)
                    openRequest.deferredResponse.complete(bytes)
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

    suspend inline infix fun transaction(fn: Communicator.() -> Unit) {
        doHandshake()
        fn()
        disconnect()
    }

    suspend inline infix operator fun invoke(fn: Communicator.() -> Unit) {
        transaction(fn)
    }

}
