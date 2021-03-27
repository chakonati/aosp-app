package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.EmptyRequest
import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Response
import org.msgpack.core.MessageUnpacker

class DeviceIdRequest : EmptyRequest<DeviceIdResponse>("KeyExchange.deviceId") {
    override fun newResponse() = DeviceIdResponse()
}

class DeviceIdResponse(var deviceId: Int = 0, var error: Error = null) : Response(2) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        deviceId = unpackInt()
    }
}