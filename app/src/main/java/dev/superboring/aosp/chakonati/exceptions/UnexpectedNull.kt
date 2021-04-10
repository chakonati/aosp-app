package dev.superboring.aosp.chakonati.exceptions

class UnexpectedNull(msg: String) : RuntimeException(
    "Unexpected null even though it shouldn't be null as per spec or previous checks: $msg"
)