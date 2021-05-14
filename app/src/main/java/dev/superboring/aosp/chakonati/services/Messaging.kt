package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.dao.relayServerPassword
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.protocol.exceptions.RequestFailure
import dev.superboring.aosp.chakonati.protocol.requests.messaging.*
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.service.OwnRelayServer
import dev.superboring.aosp.chakonati.service.RemoteService

class MessageSendFailure(error: String) :
    RequestFailure("failed to send message: $error")

class NextMessageIdRetrievalFailure(error: String) :
    RequestFailure("failed to retrieve next message ID: $error")

class MessageReceiveFailure(id: Long, error: String) :
    RequestFailure("could not receive message with ID $id: $error")

class MessageConfirmReceiveFailure(id: Long, error: String) :
    RequestFailure("could not confirm message received with ID $id: $error")

class OutboundMessageRejection(reason: String) :
    RuntimeException("rejected: $reason")

object Messaging {

    suspend fun nextMessageId(): Pair<Boolean, Long> {
        OwnRelayServer.comm.send(NextMessageIdRequest(relayServerPassword)).let {
            it.error?.let { error -> throw NextMessageIdRetrievalFailure(error) }
            return Pair(it.hasNewMessages, it.messageId)
        }
    }

    suspend fun getMessage(messageId: Long): EncryptedMessage {
        OwnRelayServer.comm.send(GetMessageRequest(messageId, relayServerPassword)).let {
            it.error?.let { error -> throw MessageReceiveFailure(messageId, error) }
            return it.message
        }
    }

    suspend fun confirmMessageReceived(messageId: Long) {
        OwnRelayServer.comm.send(ConfirmReceivedRequest(messageId, relayServerPassword)).let {
            it.error?.let { error -> throw MessageConfirmReceiveFailure(messageId, error) }
        }
    }

}

class RemoteMessaging(private val communicator: Communicator) : RemoteService(communicator) {

    suspend fun sendMessage(encryptedMessage: ByteArray) {
        if (encryptedMessage.isEmpty()) {
            throw OutboundMessageRejection("Your message is empty. Please put data in it.")
        }
        communicator.send(
            SendMessageRequest(
                encryptedMessage,
                db.mySetup().get().relayServer,
                db.mySetup().get().deviceId,
            )
        ).error?.let {
            throw MessageSendFailure(it)
        }
    }

}
