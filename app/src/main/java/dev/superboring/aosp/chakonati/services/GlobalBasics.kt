package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.protocol.requests.basic.EchoRequest
import dev.superboring.aosp.chakonati.service.OwnRelayServer

object GlobalBasics {

    suspend fun echo(value: String): String {
        return OwnRelayServer.comm.send(EchoRequest(value)).echo
    }

}
