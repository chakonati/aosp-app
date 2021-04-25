package dev.superboring.aosp.chakonati.domain

import dev.superboring.aosp.chakonati.service.ServerAddress
import kotlinx.serialization.Serializable

typealias Recipient = ServerAddress
typealias LastMessage = String

@Serializable
data class ChatSummary(
    val chatId: Long,
    val recipient: Recipient,
    val displayName: String,
    val lastMessage: LastMessage,
)
