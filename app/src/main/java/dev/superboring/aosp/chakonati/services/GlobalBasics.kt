package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.protocol.requests.EchoRequest
import dev.superboring.aosp.chakonati.service.ownRelayCommunicator

object GlobalBasics {

    suspend fun echo(value: String): String {
        return ownRelayCommunicator.send(EchoRequest(value)).echo
    }

}