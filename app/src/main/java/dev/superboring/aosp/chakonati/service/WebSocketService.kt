package dev.superboring.aosp.chakonati.service

import android.util.Log
import dev.superboring.aosp.chakonati.logging.ATP
import dev.superboring.aosp.chakonati.protocol.PackSerializable
import dev.superboring.aosp.chakonati.protocol.Packable
import dev.superboring.aosp.chakonati.protocol.requests.HelloResponse
import okhttp3.*
import java.util.concurrent.TimeUnit
import okhttp3.Response

import okhttp3.WebSocket
import okio.ByteString

private val TAG = ATP + WebSocketService::class.simpleName

class WebSocketService(private val uri: String) : WebSocketListener() {

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
        val response = HelloResponse()
        response.deserialize(bytes.toByteArray())
        println(response.reply)
    }
}