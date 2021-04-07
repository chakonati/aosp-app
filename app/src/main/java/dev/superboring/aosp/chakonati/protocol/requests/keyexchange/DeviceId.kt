package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.EmptyRequest
import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Response

class DeviceIdRequest : EmptyRequest<DeviceIdResponse>("KeyExchange.deviceId")
data class DeviceIdResponse(var deviceId: Int, var error: Error) : Response()