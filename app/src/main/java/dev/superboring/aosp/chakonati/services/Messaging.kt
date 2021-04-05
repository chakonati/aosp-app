package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.persistence.dao.relayServerPassword
import dev.superboring.aosp.chakonati.protocol.exceptions.RequestFailure
import dev.superboring.aosp.chakonati.protocol.requests.keyexchange.*
import dev.superboring.aosp.chakonati.protocol.requests.messaging.NextMessageIdRequest
import dev.superboring.aosp.chakonati.protocol.requests.messaging.SendMessageRequest
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.service.OwnRelayServer
import dev.superboring.aosp.chakonati.service.RemoteService
import org.whispersystems.libsignal.state.PreKeyBundle

class MessageSendFailure(error: String) :
        RequestFailure("failed to send message: $error")

class NextMessageIdRetrievalFailure(error: String) :
        RequestFailure("failed to retrieve next message ID: $error")

object Messaging {

    suspend fun nextMessageId(): Pair<Boolean, Long> {
        OwnRelayServer.comm.send(NextMessageIdRequest(relayServerPassword)).let {
            it.error?.let { error -> throw NextMessageIdRetrievalFailure(error) }
            return Pair(it.hasNewMessages, it.messageId)
        }
    }

}

class RemoteMessaging(private val communicator: Communicator) : RemoteService(communicator) {

    suspend fun sendMessage(encryptedMessage: ByteArray) {
        communicator.send(SendMessageRequest(encryptedMessage)).error?.let {
            throw MessageSendFailure(it)
        }
    }

}