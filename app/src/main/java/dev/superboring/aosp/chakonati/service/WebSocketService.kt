package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.extras.msgpack.serialized
import kotlinx.serialization.Serializable
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class WebSocketService(private val uri: String) : WebSocketListener() {

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

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        listeners.forEach { it.onError(t) }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        listeners.forEach { it.onMessage(bytes.toByteArray()) }
    }

    fun addListener(listener: WebSocketServiceListener) {
        listeners += listener
    }

    fun disconnect() {
        webSocket.close(1000, "normal close")
    }
}
