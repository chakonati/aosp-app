package dev.superboring.aosp.chakonati.protocol.requests.setup

import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response
import dev.superboring.aosp.chakonati.protocol.unpackError
import org.msgpack.core.MessageUnpacker

class IsPasswordSetupRequest :
    Request<IsPasswordSetupResponse>("Setup.isPasswordSetup") {
    override fun newResponse(): IsPasswordSetupResponse {
        return IsPasswordSetupResponse()
    }
}

class IsPasswordSetupResponse(var isSetup: Boolean = false) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        isSetup = unpackBoolean()
    }
}

class SetPasswordRequest :
    Request<SetPasswordResponse>("Setup.setPassword") {
    override fun newResponse(): SetPasswordResponse {
        return SetPasswordResponse()
    }
}

class SetPasswordResponse(
    var password: String = "",
    var error: Error = null
) : Response(2) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        password = unpackString()
        error = unpackError()
    }
}