package dev.superboring.aosp.chakonati.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.michaelbull.retry.policy.binaryExponentialBackoff
import com.github.michaelbull.retry.policy.constantDelay
import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.policy.plus
import com.github.michaelbull.retry.retry
import dev.superboring.aosp.chakonati.extensions.kotlin.notThen
import dev.superboring.aosp.chakonati.extensions.kotlin.then
import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.db
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

typealias ServerAddress = String

object OwnRelayServer {

    lateinit var comm: Communicator

    var connectionError by mutableStateOf(null as Exception?)
    var isConnecting by mutableStateOf(true)
    var justConnectedSuccessfully by mutableStateOf(false)

    private val mutex = Mutex()

    suspend fun prepareCommunicator() {
        retry(constantDelay(6000)) {
            isConnecting = true
            try {
                retry(limitAttempts(5) + binaryExponentialBackoff(250, 3000)) {
                    ::comm.isInitialized then { comm.disconnect() }
                    comm = Communicator(db.mySetup().get().relayServer)
                    comm.doHandshake()
                    val hadError = connectionError != null
                    isConnecting = false
                    connectionError = null
                    hadError then {
                        mutex.isLocked notThen {
                            mutex.withLock {
                                justConnectedSuccessfully = true
                                delay(1000)
                                justConnectedSuccessfully = false
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                connectionError = e
                isConnecting = false
                throw e
            }
        }
    }

}
