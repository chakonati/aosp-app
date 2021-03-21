package dev.superboring.aosp.chakonati

import dev.superboring.aosp.chakonati.service.Communicator

typealias ServerAddress = String

lateinit var ownRelayCommunicator: Communicator

val foreignRelayCommunicators = LinkedHashMap<ServerAddress, Communicator>()
