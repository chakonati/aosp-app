package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.ownRelayCommunicator
import dev.superboring.aosp.chakonati.protocol.exceptions.RequestFailure
import dev.superboring.aosp.chakonati.protocol.requests.PreKeyBundlePublishRequest
import dev.superboring.aosp.chakonati.protocol.requests.RetrievePreKeyBundleRequest
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.service.RemoteService
import org.whispersystems.libsignal.state.PreKeyBundle

class PreKeyBundlePublishFailed(error: String) :
    RequestFailure("Failed to publish pre-key bundle to the server: $error")

class PreKeyBundleRetrieveFailed(error: String) :
    RequestFailure("Failed to retrieve pre-key bundle from server: $error")

object KeyExchange {

    suspend fun publishPreKeyBundle(bundle: PreKeyBundle) {
        ownRelayCommunicator.send(PreKeyBundlePublishRequest(bundle)).error?.let { error ->
            throw PreKeyBundlePublishFailed(error)
        }
    }

}

class RemoteKeyExchange(private val communicator: Communicator) : RemoteService(communicator) {

    suspend fun preKeyBundle(): PreKeyBundle {
        communicator.send(RetrievePreKeyBundleRequest()).let {
            it.error?.let { error -> throw PreKeyBundleRetrieveFailed(error) }
            return it.bundle
        }
    }

}