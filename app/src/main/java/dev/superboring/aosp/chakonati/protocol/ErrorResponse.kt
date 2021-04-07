package dev.superboring.aosp.chakonati.protocol

abstract class ErrorResponse(val error: Error = null) : Response()