package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.ErrorResponse
import dev.superboring.aosp.chakonati.protocol.Request
import org.whispersystems.libsignal.state.PreKeyBundle

data class PreKeyBundlePublishRequest(
    val preKeyBundle: PreKeyBundle,
    val password: String,
) : Request<PreKeyBundlePublishResponse>("KeyExchange.publishPreKeyBundle")

class PreKeyBundlePublishResponse : ErrorResponse()