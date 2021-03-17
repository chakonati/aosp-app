package dev.superboring.aosp.chakonati.service

interface WebSocketServiceListener {
    fun onMessage(bytes: ByteArray)
}