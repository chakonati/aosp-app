package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.mainCommunicator
import dev.superboring.aosp.chakonati.protocol.requests.EchoRequest
import dev.superboring.aosp.chakonati.service.Communicator

object GlobalBasics {

    suspend fun echo(value: String): String {
        return mainCommunicator.send(EchoRequest(value)).echo
    }

}