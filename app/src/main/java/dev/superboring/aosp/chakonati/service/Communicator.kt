package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.BuildConfig
import dev.superboring.aosp.chakonati.extras.msgpack.deserialize
import dev.superboring.aosp.chakonati.extras.msgpack.serialized
import dev.superboring.aosp.chakonati.protocol.*
import dev.superboring.aosp.chakonati.protocol.exceptions.UnsupportedMessageType
import dev.superboring.aosp.chakonati.protocol.exceptions.UntrackedResponsePacketException
import dev.superboring.aosp.chakonati.protocol.requests.basic.HelloRequest
import dev.superboring.aosp.chakonati.services.SubscriptionName
import dev.superboring.aosp.chakonati.x.debug
import dev.superboring.aosp.chakonati.x.logging.logDebug
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

interface SubscriptionListener {
    suspend fun onNotification(bytes: ByteArray)
}

class CommunicatorRequestFailed(extra: String, e: Exception) :
    RuntimeException(extra, e)

class Communicator(private val server: String) : WebSocketServiceListener {

    private val openRequests = hashMapOf<RequestId, OpenRequest>()
    private val subscriptionListeners = hashMapOf<SubscriptionName, SubscriptionListener>()

    var hasSentHello = false
        private set

    private val serverUri
        get() = "ws${if (BuildConfig.DEBUG) "" else "s"}://$server"

    private val webSocketService by lazy {
        WebSocketService(serverUri).apply {
            addListener(this@Communicator)
        }
    }

    init {
        logDebug("New Communicator for %s", server)
    }

    suspend fun doHandshake() {
        logDebug("Doing handshake with %s", serverUri)
        val response = sendWithoutChecks(HelloRequest())
        if (response.reply != "hi!") {
            logDebug("got ${response.reply} as reply")
            throw HandshakeFailure("unexpected reply")
        }
        hasSentHello = true
        logDebug("Handshake completed successfully")
    }

    suspend inline infix fun <reified R : Response> send(request: Request<R>): R {
        if (!hasSentHello) {
            doHandshake()
        }
        return sendWithoutChecks(request)
    }

    suspend inline infix fun <reified R : Response> sendWithoutChecks(request: Request<R>): R {
        try {
            logDebug("Sending request %s", request.toString())
            return sendAsync(request).await().deserialize()
        } catch (e: Exception) {
            // oops
            logDebug(CommunicatorRequestFailed("error in sendWithoutChecks", e).toString())
            throw e
        }
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

    override suspend fun onMessage(bytes: ByteArray) {
        logDebug("Received message, length: %d", bytes.size)
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
            MessageTypes.NOTIFICATION -> {
                val notification = bytes.deserialize<Notification>()
                subscriptionListeners[notification.subscriptionName]?.onNotification(notification.data)
            }
            else -> throw UnsupportedMessageType(message.messageType)
        }
    }

    override fun onError(t: Throwable) {
        logDebug("Error: %s", t.toString())
        disconnect()
        synchronized(openRequests) {
            openRequests.forEach { it.value.deferredResponse.completeExceptionally(t) }
        }
    }

    fun setSubscriptionListener(
        subscriptionName: SubscriptionName,
        listener: SubscriptionListener,
    ) {
        subscriptionListeners[subscriptionName] = listener
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

class HandshakeFailure(reason: String) :
    RuntimeException("could not complete handshake with server: $reason")
