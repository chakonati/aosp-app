package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.logging.ATP
import dev.superboring.aosp.chakonati.protocol.PackSerializable
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

private val TAG = ATP + WebSocketService::class.simpleName

class WebSocketService(private val uri: String) : WebSocketListener() {

    private val listeners = arrayListOf<WebSocketServiceListener>()

    private val webSocket: WebSocket by lazy {
        val client = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url(uri)
            .build()
        client.newWebSocket(request, this).apply {

        }
    }

    fun send(data: PackSerializable) {
        if (!webSocket.send(ByteString.of(*data.serialize()))) {
            throw RuntimeException("Failed to send websocket message")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        listeners.forEach { it.onMessage(bytes.toByteArray()) }
    }

    fun addListener(listener: WebSocketServiceListener) {
        listeners += listener
    }
}