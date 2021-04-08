package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.protocol.requests.setup.IsPasswordSetupRequest
import dev.superboring.aosp.chakonati.protocol.requests.setup.IsPasswordValidRequest
import dev.superboring.aosp.chakonati.protocol.requests.setup.SetPasswordRequest
import dev.superboring.aosp.chakonati.service.OwnRelayServer

class SettingPasswordFailed(error: String) :
    RuntimeException("setting password failed: $error")

object Setup {

    suspend fun setPassword(): String {
        OwnRelayServer.comm.send(SetPasswordRequest()).apply {
            error?.let { throw SettingPasswordFailed(it) }
            return password
        }
    }

    suspend fun isPasswordSet(): Boolean {
        return OwnRelayServer.comm.send(IsPasswordSetupRequest()).isSetUp
    }

    suspend fun isPasswordValid(password: String): Boolean {
        return OwnRelayServer.comm.send(IsPasswordValidRequest(password)).valid
    }

}
