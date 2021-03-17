package dev.superboring.aosp.chakonati.service

import dev.superboring.aosp.chakonati.protocol.Request
import dev.superboring.aosp.chakonati.protocol.Response
import kotlinx.coroutines.CompletableDeferred

data class OpenRequest<R : Response>(
    val deferredResponse: CompletableDeferred<Response>,
    val request: Request<R>,
)