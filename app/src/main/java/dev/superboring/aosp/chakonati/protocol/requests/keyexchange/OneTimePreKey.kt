package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.EmptyRequest
import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Response

class OneTimePreKeyRequest : EmptyRequest<OneTimePreKeyResponse>("KeyExchange.oneTimePreKey")
data class OneTimePreKeyResponse(
    var key: OneTimePreKey?,
    var error: Error
) : Response()
