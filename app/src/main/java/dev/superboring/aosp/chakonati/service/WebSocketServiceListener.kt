package dev.superboring.aosp.chakonati.service

interface WebSocketServiceListener {
    suspend fun onMessage(bytes: ByteArray)
    fun onError(t: Throwable)
}
