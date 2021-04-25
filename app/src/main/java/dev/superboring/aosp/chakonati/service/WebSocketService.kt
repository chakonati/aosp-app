package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.extras.msgpack.serialized
import dev.superboring.aosp.chakonati.x.logging.logDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.serialization.Serializable
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class WebSocketService(private val uri: String) : WebSocketListener(), CoroutineScope {

    private val job by lazy { Job() }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val listeners = arrayListOf<WebSocketServiceListener>()

    private val webSocket: WebSocket by lazy {
        val client = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url(uri)
            .build()
        client.newWebSocket(request, this)
    }

    fun send(data: @Serializable Any) {
        if (!webSocket.send(ByteString.of(*data.serialized))) {
            throw RuntimeException("Failed to send WebSocket message")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        logDebug("WebSocket connection opened")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        listeners.forEach { it.onError(t) }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        listeners.forEach { launchIO { it.onMessage(bytes.toByteArray()) } }
    }

    fun addListener(listener: WebSocketServiceListener) {
        listeners += listener
    }

    fun disconnect() {
        logDebug("Disconnecting WebSocket")
        webSocket.close(1000, "normal close")
    }
}
