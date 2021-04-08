package dev.superboring.aosp.chakonati.protocol.requests.setup

import dev.superboring.aosp.chakonati.protocol.EmptyRequest
import dev.superboring.aosp.chakonati.protocol.Error
import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response

class IsPasswordSetupRequest : EmptyRequest<IsPasswordSetupResponse>("Setup.isPasswordSetup")
data class IsPasswordSetupResponse(val isSetUp: Boolean) : Response()

class SetPasswordRequest : EmptyRequest<SetPasswordResponse>("Setup.setPassword")
data class SetPasswordResponse(val password: String, val error: Error) : Response()

data class IsPasswordValidRequest(val password: String) :
    Request<IsPasswordValidResponse>("Setup.isPasswordValid")

data class IsPasswordValidResponse(val valid: Boolean) : Response()