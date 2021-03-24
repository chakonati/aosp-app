package dev.superboring.aosp.chakonati.protocol.requests.setup

import dev.superboring.aosp.chakonati.protocol.*
import org.msgpack.core.MessagePacker
import org.msgpack.core.MessageUnpacker

class IsPasswordSetupRequest :
    EmptyRequest<IsPasswordSetupResponse>("Setup.isPasswordSetup") {
    override fun newResponse() = IsPasswordSetupResponse()
}

class IsPasswordSetupResponse(var isSetup: Boolean = false) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        isSetup = unpackBoolean()
    }
}

class SetPasswordRequest :
    EmptyRequest<SetPasswordResponse>("Setup.setPassword") {
    override fun newResponse() = SetPasswordResponse()
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

class IsPasswordValidRequest(private val password: String) :
    Request<IsPasswordValidResponse>("Setup.isPasswordValid", 1) {
    override fun pack(packer: MessagePacker): Unit = packer.run {
        packString(password)
    }

    override fun newResponse() = IsPasswordValidResponse()
}

class IsPasswordValidResponse(var isValid: Boolean = false) : Response(1) {
    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        isValid = unpackBoolean()
    }

}