package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.protocol.requests.setup.IsPasswordSetupRequest
import dev.superboring.aosp.chakonati.protocol.requests.setup.SetPasswordRequest
import dev.superboring.aosp.chakonati.service.ownRelayCommunicator

class SettingPasswordFailed(error: String) :
    RuntimeException("setting password failed: $error")

object Setup {

    suspend fun setPassword(): String {
        ownRelayCommunicator.send(SetPasswordRequest()).apply {
            error?.let { throw SettingPasswordFailed(it) }
            return password
        }
    }

    suspend fun isPasswordSet(): Boolean {
        return ownRelayCommunicator.send(IsPasswordSetupRequest()).isSetup
    }

}
