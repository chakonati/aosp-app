package dev.superboring.aosp.chakonati.protocol.exceptions

abstract class RequestFailure(error: String) : RuntimeException(error)
