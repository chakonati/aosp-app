package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.mainCommunicator
import dev.superboring.aosp.chakonati.protocol.requests.PreKeyBundlePublishRequest
import org.whispersystems.libsignal.state.PreKeyBundle

class PreKeyBundlePublishFailed(error: String) :
    RuntimeException("Failed to publish pre-key bundle to the server: $error")

object KeyExchange {

    suspend fun publishPreKeyBundle(bundle: PreKeyBundle) {
        mainCommunicator.send(PreKeyBundlePublishRequest(bundle)).error?.let { error ->
            throw PreKeyBundlePublishFailed(error)
        }
    }

}