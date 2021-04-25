package dev.superboring.aosp.chakonati.extensions.t

inline fun <reified T> T.tag() = T::class.simpleName