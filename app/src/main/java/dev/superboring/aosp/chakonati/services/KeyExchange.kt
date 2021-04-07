package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.persistence.dao.relayServerPassword
import dev.superboring.aosp.chakonati.protocol.exceptions.RequestFailure
import dev.superboring.aosp.chakonati.protocol.requests.keyexchange.*
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.service.OwnRelayServer
import dev.superboring.aosp.chakonati.service.RemoteService
import org.whispersystems.libsignal.state.PreKeyBundle

class PreKeyBundlePublishFailed(error: String) :
    RequestFailure("Failed to publish pre-key bundle to the server: $error")

class PreKeyBundleRetrieveFailed(error: String) :
    RequestFailure("Failed to retrieve pre-key bundle from server: $error")

class OneTimePreKeysPublishFailed(error: String) :
    RequestFailure("Failed to publish one time pre-keys to the server: $error")

class OneTimePreKeyRetrieveFailed(error: String) :
    RequestFailure("Failed to retrieve one time pre-key from server: $error")

class DeviceIdRetrieveFailed(error: String) :
    RequestFailure("Failed to retrieve device ID from server: $error")

object KeyExchange {

    suspend fun publishPreKeyBundle(bundle: PreKeyBundle) {
        OwnRelayServer.comm.send(
            PreKeyBundlePublishRequest(
                bundle, relayServerPassword,
            )
        ).error?.let { throw PreKeyBundlePublishFailed(it) }
    }

    suspend fun publishOneTimePreKeys(preKeys: List<OneTimePreKey>) {
        OwnRelayServer.comm.send(OneTimePreKeysPublishRequest(preKeys, relayServerPassword))
            .error?.let { throw OneTimePreKeysPublishFailed(it) }
    }

}

class RemoteKeyExchange(private val communicator: Communicator) : RemoteService(communicator) {

    suspend fun preKeyBundle(): PreKeyBundle {
        communicator.send(RetrievePreKeyBundleRequest()).let {
            it.error?.let { error -> throw PreKeyBundleRetrieveFailed(error) }
            return it.preKeyBundle()
        }
    }

    suspend fun preKeyBundleExists(): Boolean {
        return communicator.send(PreKeyBundleExistsRequest()).exists
    }

    suspend fun deviceId(): Int {
        communicator.send(DeviceIdRequest()).let {
            it.error?.let { error -> throw DeviceIdRetrieveFailed(error) }
            return it.deviceId
        }
    }

    suspend fun oneTimePreKey(): OneTimePreKey? {
        communicator.send(OneTimePreKeyRequest()).let {
            it.error?.let { error -> throw OneTimePreKeyRetrieveFailed(error) }
            return it.key
        }
    }

}