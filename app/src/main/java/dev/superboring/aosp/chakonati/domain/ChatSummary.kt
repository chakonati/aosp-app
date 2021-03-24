package dev.superboring.aosp.chakonati.domain

import dev.superboring.aosp.chakonati.service.ServerAddress

typealias Recipient = ServerAddress
typealias LastMessage = String

data class ChatSummary(
    val recipient: Recipient,
    val displayName: String,
    val lastMessage: LastMessage,
)
