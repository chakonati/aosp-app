package dev.superboring.aosp.chakonati.domain

data class MessageNotification(
    val messageId: Long,
    val from: String,
    val deviceId: Int,
)