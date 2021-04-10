package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.persistence.entities.PrePublicKey
import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response

data class OneTimePreKey(
    val preKeyId: Int,
    val preKey: PrePublicKey,
)

data class OneTimePreKeysPublishRequest(
    val preKeys: List<OneTimePreKey>,
    val password: String
) : Request<OneTimePreKeysPublishResponse>("KeyExchange.publishOneTimePreKeys")

data class OneTimePreKeysPublishResponse(val error: Error) : Response()
