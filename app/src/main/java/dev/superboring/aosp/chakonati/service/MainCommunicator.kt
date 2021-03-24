package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.db

typealias ServerAddress = String

lateinit var ownRelayCommunicator: Communicator

var isOwnRelayServerUsable = false

suspend fun prepareOwnRelayCommunicator() {
    val newCommunicator = Communicator(db.mySetup().get().relayServer)
    if (::ownRelayCommunicator.isInitialized) {
        ownRelayCommunicator.disconnect()
    }
    ownRelayCommunicator = newCommunicator
    ownRelayCommunicator.doHandshake()
}

val foreignRelayCommunicators = LinkedHashMap<ServerAddress, Communicator>()
