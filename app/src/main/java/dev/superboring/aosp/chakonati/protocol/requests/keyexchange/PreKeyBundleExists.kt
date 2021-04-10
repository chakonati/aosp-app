package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.EmptyRequest
import dev.superboring.aosp.chakonati.protocol.Response

class PreKeyBundleExistsRequest :
    EmptyRequest<PreKeyBundleExistsResponse>("KeyExchange.preKeyBundleExists")

data class PreKeyBundleExistsResponse(val exists: Boolean) : Response()
