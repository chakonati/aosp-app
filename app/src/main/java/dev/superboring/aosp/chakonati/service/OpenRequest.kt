package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.protocol.Request
import kotlinx.coroutines.CompletableDeferred

data class OpenRequest(
    val deferredResponse: CompletableDeferred<ByteArray>,
    val request: Request<*>,
)
